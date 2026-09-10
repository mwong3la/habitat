package com.habitat.property;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

	List<Property> findByOrganization_IdOrderByNameAsc(UUID organizationId);

	Optional<Property> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
