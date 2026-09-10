package com.habitat.organization;

import com.habitat.common.domain.ResourceNotFoundException;
import com.habitat.identity.AppUser;
import com.habitat.identity.AppUserRepository;
import com.habitat.identity.Role;
import com.habitat.identity.RoleName;
import com.habitat.identity.RoleRepository;
import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.subscription.SubscriptionService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationOnboardingService {

	private final OrganizationRepository organizationRepository;
	private final CountryRepository countryRepository;
	private final AppUserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final SubscriptionService subscriptionService;
	private final ApplicationEventPublisher events;

	public OrganizationOnboardingService(
			OrganizationRepository organizationRepository,
			CountryRepository countryRepository,
			AppUserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			SubscriptionService subscriptionService,
			ApplicationEventPublisher events) {
		this.organizationRepository = organizationRepository;
		this.countryRepository = countryRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.subscriptionService = subscriptionService;
		this.events = events;
	}

	@Transactional
	public OrganizationResponse register(RegisterOrganizationRequest request) {
		if (userRepository.existsByEmailIgnoreCase(request.adminEmail())) {
			throw new BusinessRuleViolationException("A user with this email already exists");
		}

		Country country = countryRepository.findByCode(request.countryCode().toUpperCase())
				.orElseThrow(() -> new ResourceNotFoundException("Country is not supported: " + request.countryCode()));

		Organization organization = organizationRepository.save(new Organization(
				request.name(),
				request.organizationType(),
				country,
				request.packageCode()));
		subscriptionService.grantPlanFeatures(organization, request.packageCode());

		Role adminRole = roleRepository.findByName(RoleName.ORG_ADMIN)
				.orElseThrow(() -> new ResourceNotFoundException("Organization admin role is not configured"));

		AppUser admin = new AppUser(
				organization,
				request.adminFullName(),
				request.adminEmail(),
				passwordEncoder.encode(request.adminPassword()));
		admin.addRole(adminRole);
		userRepository.save(admin);

		organization.activate();
		events.publishEvent(new OrganizationCreatedEvent(organization.getId()));
		events.publishEvent(new OrganizationActivatedEvent(organization.getId()));

		return OrganizationResponse.from(organization, subscriptionService.enabledFeatureCodes(organization));
	}
}
