package com.habitat.habitat.identity;

import com.habitat.habitat.common.domain.ResourceNotFoundException;
import com.habitat.habitat.organization.Organization;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TenantContext {

	private final AppUserRepository userRepository;

	public TenantContext(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public AppUser currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("Authentication is required");
		}

		return userRepository.findByEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user does not exist"));
	}

	@Transactional(readOnly = true)
	public Organization currentOrganization() {
		return currentUser().getOrganization();
	}

	@Transactional(readOnly = true)
	public UUID currentOrganizationId() {
		return currentOrganization().getId();
	}

	@Transactional(readOnly = true)
	public void requireRole(RoleName roleName) {
		if (!currentUser().hasRole(roleName)) {
			throw new AccessDeniedException("Required role: " + roleName.name());
		}
	}
}
