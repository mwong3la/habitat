package com.habitat.habitat.subscription;

import com.habitat.habitat.common.domain.BusinessRuleViolationException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class EntitlementCatalog {

	public Set<String> defaultFeaturesFor(String packageCode) {
		return switch (packageCode.toUpperCase()) {
			case "STARTER" -> Set.of("listing:shareable-link");
			case "GROWTH" -> Set.of("listing:shareable-link", "listing:platform-publishing");
			case "PREMIUM" -> Set.of(
					"listing:shareable-link",
					"listing:platform-publishing",
					"listing:marketplace",
					"listing:external-syndication");
			default -> throw new BusinessRuleViolationException("Unsupported package: " + packageCode);
		};
	}
}
