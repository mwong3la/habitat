package com.habitat.habitat.property;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

	List<Unit> findByProperty_IdAndOrganization_IdOrderByNameAsc(UUID propertyId, UUID organizationId);

	Optional<Unit> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
