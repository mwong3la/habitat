package com.habitat.habitat.property;

import java.util.UUID;

public record OwnerResponse(UUID id, String fullName, String email, String phoneNumber) {

	public static OwnerResponse from(Owner owner) {
		return new OwnerResponse(owner.getId(), owner.getFullName(), owner.getEmail(), owner.getPhoneNumber());
	}
}
