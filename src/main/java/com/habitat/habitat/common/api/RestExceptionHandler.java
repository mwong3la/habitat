package com.habitat.habitat.common.api;

import com.habitat.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.habitat.common.domain.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(BusinessRuleViolationException.class)
	ResponseEntity<ApiError> handleBusinessRuleViolation(BusinessRuleViolationException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception) {
		return error(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception) {
		return error(HttpStatus.FORBIDDEN, exception.getMessage(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> errors = new HashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return error(HttpStatus.BAD_REQUEST, "Validation failed", errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
	}

	private ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> validationErrors) {
		return ResponseEntity.status(status)
				.body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, validationErrors));
	}
}
