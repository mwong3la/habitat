package com.habitat.property;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateUnitRequest(
		@NotBlank @Size(max = 80) String name,
		@NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal monthlyRent) {
}
