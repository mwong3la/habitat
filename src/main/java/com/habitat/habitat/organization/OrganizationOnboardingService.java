package com.habitat.habitat.organization;

import com.habitat.habitat.common.domain.ResourceNotFoundException;
import com.habitat.habitat.identity.AppUser;
import com.habitat.habitat.identity.AppUserRepository;
import com.habitat.habitat.identity.Role;
import com.habitat.habitat.identity.RoleName;
import com.habitat.habitat.identity.RoleRepository;
import com.habitat.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.habitat.subscription.EntitlementCatalog;
import java.util.Set;
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
	private final EntitlementCatalog entitlementCatalog;
	private final ApplicationEventPublisher events;

	public OrganizationOnboardingService(
			OrganizationRepository organizationRepository,
			CountryRepository countryRepository,
			AppUserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder,
			EntitlementCatalog entitlementCatalog,
			ApplicationEventPublisher events) {
		this.organizationRepository = organizationRepository;
		this.countryRepository = countryRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.entitlementCatalog = entitlementCatalog;
		this.events = events;
	}

	@Transactional
	public OrganizationResponse register(RegisterOrganizationRequest request) {
		if (userRepository.existsByEmailIgnoreCase(request.adminEmail())) {
			throw new BusinessRuleViolationException("A user with this email already exists");
		}

		Country country = countryRepository.findByCode(request.countryCode().toUpperCase())
				.orElseThrow(() -> new ResourceNotFoundException("Country is not supported: " + request.countryCode()));

		Set<String> enabledFeatures = entitlementCatalog.defaultFeaturesFor(request.packageCode());
		Organization organization = organizationRepository.save(new Organization(
				request.name(),
				request.organizationType(),
				country,
				request.packageCode(),
				enabledFeatures));

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

		return OrganizationResponse.from(organization);
	}
}
