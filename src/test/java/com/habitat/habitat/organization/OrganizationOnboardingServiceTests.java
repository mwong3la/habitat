package com.habitat.habitat.organization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.habitat.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.habitat.identity.AppUserRepository;
import com.habitat.habitat.identity.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrganizationOnboardingServiceTests {

	@Autowired
	OrganizationOnboardingService onboardingService;

	@Autowired
	AppUserRepository userRepository;

	@Test
	void registersOrganizationAndAdminInSelectedCountry() {
		OrganizationResponse response = onboardingService.register(new RegisterOrganizationRequest(
				"Habitat Managers",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"Ada Admin",
				"ada@example.com",
				"very-secure-password"));

		assertThat(response.status()).isEqualTo(OrganizationStatus.LIVE);
		assertThat(response.baseCurrencyCode()).isEqualTo("KES");
		assertThat(response.enabledFeatures()).containsExactly("listing:shareable-link");
		assertThat(userRepository.findByEmailIgnoreCase("ada@example.com"))
				.hasValueSatisfying(user -> assertThat(user.hasRole(RoleName.ORG_ADMIN)).isTrue());
	}

	@Test
	void rejectsDuplicateAdminEmailAcrossOrganizations() {
		onboardingService.register(new RegisterOrganizationRequest(
				"First Org",
				OrganizationType.LANDLORD,
				"US",
				"GROWTH",
				"Sam Owner",
				"sam@example.com",
				"very-secure-password"));

		assertThatThrownBy(() -> onboardingService.register(new RegisterOrganizationRequest(
				"Second Org",
				OrganizationType.AGENCY,
				"KE",
				"STARTER",
				"Sam Again",
				"sam@example.com",
				"very-secure-password")))
				.isInstanceOf(BusinessRuleViolationException.class)
				.hasMessageContaining("email already exists");
	}
}
