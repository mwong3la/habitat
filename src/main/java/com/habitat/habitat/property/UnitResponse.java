package com.habitat.habitat.property;

import java.math.BigDecimal;
import java.util.UUID;

public record UnitResponse(UUID id, UUID propertyId, String name, BigDecimal monthlyRent, UnitStatus status) {

	public static UnitResponse from(Unit unit) {
		return new UnitResponse(
				unit.getId(),
				unit.getProperty().getId(),
				unit.getName(),
				unit.getMonthlyRent(),
				unit.getStatus());
	}
}
