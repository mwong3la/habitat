package com.habitat.subscription;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFeatureRepository extends JpaRepository<PlanFeature, UUID> {

	boolean existsByPlanAndFeature(Plan plan, Feature feature);

	List<PlanFeature> findByPlan_Code(String planCode);
}
