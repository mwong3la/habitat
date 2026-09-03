package com.habitat.habitat.organization;

import java.util.Set;
import java.util.UUID;

public record OrganizationResponse(
		UUID id,
		String name,
		OrganizationType type,
		String countryCode,
		String baseCurrencyCode,
		String packageCode,
		Set<String> enabledFeatures,
		OrganizationStatus status) {

	public static OrganizationResponse from(Organization organization) {
		return new OrganizationResponse(
				organization.getId(),
				organization.getName(),
				organization.getType(),
				organization.getCountry().getCode(),
				organization.getBaseCurrency().getCode(),
				organization.getPackageCode(),
				organization.getEnabledFeatures(),
				organization.getStatus());
	}
}
