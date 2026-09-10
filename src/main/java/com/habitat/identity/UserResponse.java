package com.habitat.identity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
		UUID id,
		UUID organizationId,
		String fullName,
		String email,
		Set<RoleName> roles,
		boolean enabled) {

	public static UserResponse from(AppUser user) {
		return new UserResponse(
				user.getId(),
				user.getOrganization().getId(),
				user.getFullName(),
				user.getEmail(),
				user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
				user.isEnabled());
	}
}
