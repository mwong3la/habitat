package com.habitat.habitat.common.seed;

import com.habitat.habitat.identity.Permission;
import com.habitat.habitat.identity.PermissionRepository;
import com.habitat.habitat.identity.Role;
import com.habitat.habitat.identity.RoleName;
import com.habitat.habitat.identity.RoleRepository;
import com.habitat.habitat.organization.Country;
import com.habitat.habitat.organization.CountryRepository;
import com.habitat.habitat.organization.Currency;
import com.habitat.habitat.organization.CurrencyRepository;
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

	public ReferenceDataSeeder(
			CurrencyRepository currencyRepository,
			CountryRepository countryRepository,
			PermissionRepository permissionRepository,
			RoleRepository roleRepository) {
		this.currencyRepository = currencyRepository;
		this.countryRepository = countryRepository;
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
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
}
