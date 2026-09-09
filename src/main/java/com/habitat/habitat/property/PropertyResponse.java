package com.habitat.habitat.property;

import java.util.UUID;

public record PropertyResponse(
		UUID id,
		UUID ownerId,
		String name,
		String addressLine1,
		String city,
		String region) {

	public static PropertyResponse from(Property property) {
		UUID ownerId = property.getOwner() == null ? null : property.getOwner().getId();
		return new PropertyResponse(
				property.getId(),
				ownerId,
				property.getName(),
				property.getAddressLine1(),
				property.getCity(),
				property.getRegion());
	}
}
