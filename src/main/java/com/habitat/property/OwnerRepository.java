package com.habitat.property;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

	Optional<Owner> findByIdAndOrganization_Id(UUID id, UUID organizationId);

	boolean existsByOrganization_IdAndEmailIgnoreCase(UUID organizationId, String email);
}
