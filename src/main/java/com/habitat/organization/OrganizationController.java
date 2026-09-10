package com.habitat.organization;

import com.habitat.identity.RoleName;
import com.habitat.identity.TenantContext;
import com.habitat.subscription.SubscriptionService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

	private final OrganizationOnboardingService onboardingService;
	private final TenantContext tenantContext;
	private final SubscriptionService subscriptionService;

	public OrganizationController(
			OrganizationOnboardingService onboardingService,
			TenantContext tenantContext,
			SubscriptionService subscriptionService) {
		this.onboardingService = onboardingService;
		this.tenantContext = tenantContext;
		this.subscriptionService = subscriptionService;
	}

	@PostMapping
	public ResponseEntity<OrganizationResponse> register(@Valid @RequestBody RegisterOrganizationRequest request) {
		OrganizationResponse response = onboardingService.register(request);
		return ResponseEntity.created(URI.create("/api/v1/organizations/" + response.id())).body(response);
	}

	@GetMapping("/me")
	@Transactional(readOnly = true)
	public OrganizationResponse currentOrganization() {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		Organization organization = tenantContext.currentOrganization();
		return OrganizationResponse.from(organization, subscriptionService.enabledFeatureCodes(organization));
	}
}
