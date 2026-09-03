package com.habitat.habitat.identity;

import com.habitat.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.habitat.common.domain.ResourceNotFoundException;
import com.habitat.habitat.organization.Organization;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class StaffManagementService {

	private final AppUserRepository userRepository;
	private final RoleRepository roleRepository;
	private final TenantContext tenantContext;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher events;

	public StaffManagementService(
			AppUserRepository userRepository,
			RoleRepository roleRepository,
			TenantContext tenantContext,
			PasswordEncoder passwordEncoder,
			ApplicationEventPublisher events) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.tenantContext = tenantContext;
		this.passwordEncoder = passwordEncoder;
		this.events = events;
	}

	@Transactional
	public UserResponse inviteStaff(@Valid InviteStaffRequest request) {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		if (request.roles().contains(RoleName.TENANT) || request.roles().contains(RoleName.OWNER)) {
			throw new BusinessRuleViolationException("Tenant and owner portal users are not invited as staff");
		}
		if (userRepository.existsByEmailIgnoreCase(request.email())) {
			throw new BusinessRuleViolationException("A user with this email already exists");
		}

		Organization organization = tenantContext.currentOrganization();
		Set<Role> roles = request.roles().stream()
				.map(this::role)
				.collect(Collectors.toSet());
		AppUser user = new AppUser(
				organization,
				request.fullName(),
				request.email(),
				passwordEncoder.encode(request.temporaryPassword()));
		roles.forEach(user::addRole);

		AppUser saved = userRepository.save(user);
		events.publishEvent(new StaffInvitedEvent(organization.getId(), saved.getId()));
		return UserResponse.from(saved);
	}

	private Role role(RoleName roleName) {
		return roleRepository.findByName(roleName)
				.orElseThrow(() -> new ResourceNotFoundException("Role is not configured: " + roleName));
	}
}
