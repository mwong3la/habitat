package com.habitat.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.organization.OrganizationOnboardingService;
import com.habitat.organization.OrganizationType;
import com.habitat.organization.RegisterOrganizationRequest;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class StaffManagementServiceTests {

	@Autowired
	OrganizationOnboardingService onboardingService;

	@Autowired
	StaffManagementService staffManagementService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void organizationAdminCanInviteStaffIntoTheirOrganization() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Staff Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"GROWTH",
				"Admin User",
				"staff-admin@example.com",
				"very-secure-password"));
		authenticate("staff-admin@example.com");

		UserResponse response = staffManagementService.inviteStaff(new InviteStaffRequest(
				"Leasing Person",
				"leasing@example.com",
				"temporary-pass-123",
				Set.of(RoleName.LEASING_AGENT)));

		assertThat(response.email()).isEqualTo("leasing@example.com");
		assertThat(response.roles()).containsExactly(RoleName.LEASING_AGENT);
	}

	@Test
	void nonAdminCannotInviteStaff() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Restricted Org",
				OrganizationType.PROPERTY_MANAGER,
				"US",
				"STARTER",
				"Admin User",
				"restricted-admin@example.com",
				"very-secure-password"));
		authenticate("restricted-admin@example.com");
		staffManagementService.inviteStaff(new InviteStaffRequest(
				"Accountant User",
				"accountant@example.com",
				"temporary-pass-123",
				Set.of(RoleName.ACCOUNTANT)));
		authenticate("accountant@example.com");

		assertThatThrownBy(() -> staffManagementService.inviteStaff(new InviteStaffRequest(
				"Another User",
				"another@example.com",
				"temporary-pass-123",
				Set.of(RoleName.LEASING_AGENT))))
				.isInstanceOf(AccessDeniedException.class);
	}

	@Test
	void staffInvitationRejectsTenantPortalRole() {
		onboardingService.register(new RegisterOrganizationRequest(
				"Portal Org",
				OrganizationType.PROPERTY_MANAGER,
				"KE",
				"STARTER",
				"Portal Admin",
				"portal-admin@example.com",
				"very-secure-password"));
		authenticate("portal-admin@example.com");

		assertThatThrownBy(() -> staffManagementService.inviteStaff(new InviteStaffRequest(
				"Tenant User",
				"tenant-user@example.com",
				"temporary-pass-123",
				Set.of(RoleName.TENANT))))
				.isInstanceOf(BusinessRuleViolationException.class)
				.hasMessageContaining("not invited as staff");
	}

	private void authenticate(String email) {
		SecurityContextHolder.getContext().setAuthentication(
				UsernamePasswordAuthenticationToken.authenticated(email, "ignored", List.of()));
	}
}
