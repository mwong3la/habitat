package com.habitat.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.organization.OrganizationOnboardingService;
import com.habitat.organization.OrganizationType;
import com.habitat.organization.RegisterOrganizationRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class SubscriptionServiceTests {

	@Autowired
	OrganizationOnboardingService onboardingService;

	@Autowired
	SubscriptionService subscriptionService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void upgradeTakesEffectImmediatelyAndGrantsPlanFeatures() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Upgrade Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"Upgrade Admin",
				"upgrade-admin@example.com",
				"very-secure-password"));
		authenticate("upgrade-admin@example.com");

		OrganizationSubscriptionResponse response = subscriptionService.changePlan(new ChangePlanRequest(
				"PREMIUM",
				LocalDate.now().plusMonths(1)));

		assertThat(response.activePlanCode()).isEqualTo("PREMIUM");
		assertThat(response.pendingPlanCode()).isNull();
		assertThat(response.enabledFeatures()).contains(
				"listing:shareable-link",
				"listing:platform-publishing",
				"listing:marketplace",
				"listing:external-syndication",
				"owner:portal");
	}

	@Test
	void downgradeIsScheduledAndCurrentFeaturesRemainActive() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Downgrade Org",
				OrganizationType.PROPERTY_MANAGER,
				"US",
				"PREMIUM",
				"Downgrade Admin",
				"downgrade-admin@example.com",
				"very-secure-password"));
		authenticate("downgrade-admin@example.com");
		LocalDate nextBillingDate = LocalDate.now().plusMonths(1);

		OrganizationSubscriptionResponse response = subscriptionService.changePlan(new ChangePlanRequest(
				"STARTER",
				nextBillingDate));

		assertThat(response.activePlanCode()).isEqualTo("PREMIUM");
		assertThat(response.pendingPlanCode()).isEqualTo("STARTER");
		assertThat(response.pendingPlanEffectiveDate()).isEqualTo(nextBillingDate);
		assertThat(response.enabledFeatures()).contains("listing:marketplace");
	}

	@Test
	void disabledFeatureIsRejected() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Feature Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"Feature Admin",
				"feature-admin@example.com",
				"very-secure-password"));
		authenticate("feature-admin@example.com");

		assertThatThrownBy(() -> subscriptionService.requireFeature("listing:marketplace"))
				.isInstanceOf(BusinessRuleViolationException.class)
				.hasMessageContaining("Feature is not enabled");
	}

	private void authenticate(String email) {
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(email, "ignored", List.of()));
	}
}
