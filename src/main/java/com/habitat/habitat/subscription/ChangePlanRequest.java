package com.habitat.habitat.subscription;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record ChangePlanRequest(
		@NotBlank @Pattern(regexp = "[A-Za-z_]{3,40}") String planCode,
		@NotNull @FutureOrPresent LocalDate nextBillingDate) {
}
