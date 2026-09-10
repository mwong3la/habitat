package com.habitat.common.seed;

import com.habitat.identity.Permission;
import com.habitat.identity.PermissionRepository;
import com.habitat.identity.Role;
import com.habitat.identity.RoleName;
import com.habitat.identity.RoleRepository;
import com.habitat.organization.Country;
import com.habitat.organization.CountryRepository;
import com.habitat.organization.Currency;
import com.habitat.organization.CurrencyRepository;
import com.habitat.subscription.Feature;
import com.habitat.subscription.FeatureRepository;
import com.habitat.subscription.Plan;
import com.habitat.subscription.PlanFeature;
import com.habitat.subscription.PlanFeatureRepository;
import com.habitat.subscription.PlanRepository;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReferenceDataSeeder implements CommandLineRunner {

	private final CurrencyRepository currencyRepository;
	private final CountryRepository countryRepository;
	private final PermissionRepository permissionRepository;
	private final RoleRepository roleRepository;
	private final FeatureRepository featureRepository;
	private final PlanRepository planRepository;
	private final PlanFeatureRepository planFeatureRepository;

	public ReferenceDataSeeder(
			CurrencyRepository currencyRepository,
			CountryRepository countryRepository,
			PermissionRepository permissionRepository,
			RoleRepository roleRepository,
			FeatureRepository featureRepository,
			PlanRepository planRepository,
			PlanFeatureRepository planFeatureRepository) {
		this.currencyRepository = currencyRepository;
		this.countryRepository = countryRepository;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.featureRepository = featureRepository;
		this.planRepository = planRepository;
		this.planFeatureRepository = planFeatureRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		Currency kes = currency("KES", "Kenyan Shilling");
		Currency ngn = currency("NGN", "Nigerian Naira");
		Currency zar = currency("ZAR", "South African Rand");
		Currency usd = currency("USD", "US Dollar");

		country("KE", "Kenya", kes);
		country("NG", "Nigeria", ngn);
		country("ZA", "South Africa", zar);
		country("US", "United States", usd);

		Map<RoleName, Set<String>> permissionsByRole = Map.of(
				RoleName.ORG_ADMIN, Set.of("organization:manage", "staff:invite", "roles:assign"),
				RoleName.LEASING_AGENT, Set.of("leasing:manage", "listing:manage"),
				RoleName.ACCOUNTANT, Set.of("billing:manage", "ledger:view", "owner-statements:manage"),
				RoleName.CARETAKER, Set.of("maintenance:triage"),
				RoleName.VENDOR, Set.of("work-order:assigned:view", "work-order:assigned:update"),
				RoleName.OWNER, Set.of("owner-statement:view"),
				RoleName.TENANT, Set.of("lease:view", "invoice:view", "maintenance:create"));

		permissionsByRole.forEach(this::role);

		Feature shareableLink = feature("listing:shareable-link", "Shareable listing link");
		Feature platformPublishing = feature("listing:platform-publishing", "Listing platform publishing");
		Feature marketplace = feature("listing:marketplace", "Marketplace listing visibility");
		Feature externalSyndication = feature("listing:external-syndication", "External listing syndication");
		Feature ownerPortal = feature("owner:portal", "Owner portal access");

		Plan starter = plan("STARTER", "Starter", 1);
		Plan growth = plan("GROWTH", "Growth", 2);
		Plan premium = plan("PREMIUM", "Premium", 3);

		planFeature(starter, shareableLink);
		planFeature(growth, shareableLink);
		planFeature(growth, platformPublishing);
		planFeature(premium, shareableLink);
		planFeature(premium, platformPublishing);
		planFeature(premium, marketplace);
		planFeature(premium, externalSyndication);
		planFeature(premium, ownerPortal);
	}

	private Currency currency(String code, String name) {
		return currencyRepository.findByCode(code)
				.orElseGet(() -> currencyRepository.save(new Currency(code, name)));
	}

	private Country country(String code, String name, Currency currency) {
		return countryRepository.findByCode(code)
				.orElseGet(() -> countryRepository.save(new Country(code, name, currency)));
	}

	private Role role(RoleName roleName, Set<String> permissionCodes) {
		Role role = roleRepository.findByName(roleName)
				.orElseGet(() -> roleRepository.save(new Role(roleName)));
		permissionCodes.stream()
				.map(this::permission)
				.forEach(role::addPermission);
		return roleRepository.save(role);
	}

	private Permission permission(String code) {
		return permissionRepository.findByCode(code)
				.orElseGet(() -> permissionRepository.save(new Permission(code)));
	}

	private Feature feature(String code, String name) {
		return featureRepository.findByCode(code)
				.orElseGet(() -> featureRepository.save(new Feature(code, name)));
	}

	private Plan plan(String code, String name, int displayOrder) {
		return planRepository.findByCode(code)
				.orElseGet(() -> planRepository.save(new Plan(code, name, displayOrder)));
	}

	private void planFeature(Plan plan, Feature feature) {
		if (!planFeatureRepository.existsByPlanAndFeature(plan, feature)) {
			planFeatureRepository.save(new PlanFeature(plan, feature));
		}
	}
}
