package com.habitat.subscription;

import com.habitat.organization.Organization;
import java.time.LocalDate;
import java.util.Set;

public record OrganizationSubscriptionResponse(
		String activePlanCode,
		String pendingPlanCode,
		LocalDate pendingPlanEffectiveDate,
		Set<String> enabledFeatures) {

	public static OrganizationSubscriptionResponse from(Organization organization, Set<String> enabledFeatures) {
		return new OrganizationSubscriptionResponse(
				organization.getPackageCode(),
				organization.getPendingPackageCode(),
				organization.getPendingPackageEffectiveDate(),
				enabledFeatures);
	}
}
