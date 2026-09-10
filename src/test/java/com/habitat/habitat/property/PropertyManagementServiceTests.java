package com.habitat.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.habitat.common.domain.ResourceNotFoundException;
import com.habitat.organization.OrganizationOnboardingService;
import com.habitat.organization.OrganizationType;
import com.habitat.organization.RegisterOrganizationRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class PropertyManagementServiceTests {

	@Autowired
	OrganizationOnboardingService onboardingService;

	@Autowired
	PropertyManagementService propertyManagementService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void createsOwnerPropertyAndVacantUnitInCurrentOrganization() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Property Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"GROWTH",
				"Property Admin",
				"property-admin@example.com",
				"very-secure-password"));
		authenticate("property-admin@example.com");

		OwnerResponse owner = propertyManagementService.createOwner(new CreateOwnerRequest(
				"Mary Owner",
				"mary-owner@example.com",
				"+254700000000"));
		PropertyResponse property = propertyManagementService.createProperty(new CreatePropertyRequest(
				owner.id(),
				"Riverside Court",
				"10 Riverside Drive",
				"Nairobi",
				"Nairobi County"));
		UnitResponse unit = propertyManagementService.createUnit(
				property.id(),
				new CreateUnitRequest("A1", new BigDecimal("45000.00")));

		assertThat(property.ownerId()).isEqualTo(owner.id());
		assertThat(unit.propertyId()).isEqualTo(property.id());
		assertThat(unit.status()).isEqualTo(UnitStatus.VACANT);
	}

	@Test
	void rejectsPropertyCreationWithOwnerFromAnotherOrganization() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Owner Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"Owner Admin",
				"owner-org-admin@example.com",
				"very-secure-password"));
		authenticate("owner-org-admin@example.com");
		OwnerResponse owner = propertyManagementService.createOwner(new CreateOwnerRequest(
				"External Owner",
				"external-owner@example.com",
				null));

		onboardingService.register(new RegisterOrganizationRequest(
				"Other Property Org",
				OrganizationType.PROPERTY_MANAGER,
				"US",
				"STARTER",
				"Other Admin",
				"other-property-admin@example.com",
				"very-secure-password"));
		authenticate("other-property-admin@example.com");

		assertThatThrownBy(() -> propertyManagementService.createProperty(new CreatePropertyRequest(
				owner.id(),
				"Blocked Building",
				"1 Blocked Street",
				"Boston",
				"Massachusetts")))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Owner does not exist in this organization");
	}

	@Test
	void unitStateTransitionsAreExplicit() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Unit State Org",
				OrganizationType.PROPERTY_MANAGER,
				"ZA",
				"STARTER",
				"Unit Admin",
				"unit-admin@example.com",
				"very-secure-password"));
		authenticate("unit-admin@example.com");
		PropertyResponse property = propertyManagementService.createProperty(new CreatePropertyRequest(
				null,
				"State House",
				"20 State Road",
				"Cape Town",
				"Western Cape"));
		UnitResponse unit = propertyManagementService.createUnit(
				property.id(),
				new CreateUnitRequest("B2", new BigDecimal("9000.00")));

		assertThat(unit.status()).isEqualTo(UnitStatus.VACANT);
	}

	private void authenticate(String email) {
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(email, "ignored", List.of()));
	}
}
