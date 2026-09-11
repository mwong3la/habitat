package com.habitat.identity;

import static org.assertj.core.api.Assertions.assertThat;

import com.habitat.organization.OrganizationOnboardingService;
import com.habitat.organization.OrganizationResponse;
import com.habitat.organization.OrganizationType;
import com.habitat.organization.RegisterOrganizationRequest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class TenantContextTests {

	@Autowired
	OrganizationOnboardingService onboardingService;

	@Autowired
	TenantContext tenantContext;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void resolvesOrganizationFromAuthenticatedUserRatherThanClientInput() {
		OrganizationResponse first = onboardingService.register(new RegisterOrganizationRequest(
				"First Habitat",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"First Admin",
				"first@example.com",
				"very-secure-password"));
		onboardingService.register(new RegisterOrganizationRequest(
				"Second Habitat",
				OrganizationType.PROPERTY_MANAGER,
				"US",
				"PREMIUM",
				"Second Admin",
				"second@example.com",
				"very-secure-password"));

		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated("first@example.com", "ignored", List.of()));

		assertThat(tenantContext.currentOrganizationId()).isEqualTo(first.id());
	}
}
