package com.habitat.habitat.subscription;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, UUID> {

	Optional<Feature> findByCode(String code);
}
