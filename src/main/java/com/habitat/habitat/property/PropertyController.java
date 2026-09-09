package com.habitat.habitat.property;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

	private final PropertyManagementService propertyManagementService;

	public PropertyController(PropertyManagementService propertyManagementService) {
		this.propertyManagementService = propertyManagementService;
	}

	@PostMapping
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody CreatePropertyRequest request) {
		PropertyResponse response = propertyManagementService.createProperty(request);
		return ResponseEntity.created(URI.create("/api/v1/properties/" + response.id())).body(response);
	}

	@GetMapping
	public List<PropertyResponse> list() {
		return propertyManagementService.listProperties();
	}

	@PostMapping("/{propertyId}/units")
	public ResponseEntity<UnitResponse> createUnit(
			@PathVariable UUID propertyId,
			@Valid @RequestBody CreateUnitRequest request) {
		UnitResponse response = propertyManagementService.createUnit(propertyId, request);
		return ResponseEntity.created(URI.create("/api/v1/units/" + response.id())).body(response);
	}

	@GetMapping("/{propertyId}/units")
	public List<UnitResponse> listUnits(@PathVariable UUID propertyId) {
		return propertyManagementService.listUnits(propertyId);
	}
}
