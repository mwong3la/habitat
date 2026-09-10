package com.habitat.subscription;

import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.common.domain.ResourceNotFoundException;
import com.habitat.identity.RoleName;
import com.habitat.identity.TenantContext;
import com.habitat.organization.Organization;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {

	private final PlanRepository planRepository;
	private final PlanFeatureRepository planFeatureRepository;
	private final OrganizationFeatureRepository organizationFeatureRepository;
	private final TenantContext tenantContext;

	public SubscriptionService(
			PlanRepository planRepository,
			PlanFeatureRepository planFeatureRepository,
			OrganizationFeatureRepository organizationFeatureRepository,
			TenantContext tenantContext) {
		this.planRepository = planRepository;
		this.planFeatureRepository = planFeatureRepository;
		this.organizationFeatureRepository = organizationFeatureRepository;
		this.tenantContext = tenantContext;
	}

	@Transactional
	public void grantPlanFeatures(Organization organization, String planCode) {
		Plan plan = plan(planCode);
		organizationFeatureRepository.deleteByOrganization(organization);
		planFeatureRepository.findByPlan_Code(plan.getCode())
				.forEach(planFeature -> organizationFeatureRepository.save(
						new OrganizationFeature(organization, planFeature.getFeature())));
	}

	@Transactional(readOnly = true)
	public Set<String> enabledFeatureCodes(Organization organization) {
		return organizationFeatureRepository.findByOrganization_Id(organization.getId()).stream()
				.map(organizationFeature -> organizationFeature.getFeature().getCode())
				.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public OrganizationSubscriptionResponse currentSubscription() {
		Organization organization = tenantContext.currentOrganization();
		return OrganizationSubscriptionResponse.from(organization, enabledFeatureCodes(organization));
	}

	@Transactional(readOnly = true)
	public void requireFeature(String featureCode) {
		if (!organizationFeatureRepository.existsByOrganization_IdAndFeature_Code(tenantContext.currentOrganizationId(), featureCode)) {
			throw new BusinessRuleViolationException("Feature is not enabled: " + featureCode);
		}
	}

	@Transactional
	public OrganizationSubscriptionResponse changePlan(ChangePlanRequest request) {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		Organization organization = tenantContext.currentOrganization();
		Plan currentPlan = plan(organization.getPackageCode());
		Plan requestedPlan = plan(request.planCode());

		if (requestedPlan.getCode().equals(currentPlan.getCode())) {
			return OrganizationSubscriptionResponse.from(organization, enabledFeatureCodes(organization));
		}

		if (requestedPlan.getDisplayOrder() > currentPlan.getDisplayOrder()) {
			organization.changePackage(requestedPlan.getCode());
			organization.clearPendingPackageChange();
			grantPlanFeatures(organization, requestedPlan.getCode());
		} else {
			organization.schedulePackageDowngrade(requestedPlan.getCode(), request.nextBillingDate());
		}

		return OrganizationSubscriptionResponse.from(organization, enabledFeatureCodes(organization));
	}

	private Plan plan(String planCode) {
		return planRepository.findByCode(planCode.toUpperCase())
				.orElseThrow(() -> new ResourceNotFoundException("Plan is not configured: " + planCode));
	}
}
