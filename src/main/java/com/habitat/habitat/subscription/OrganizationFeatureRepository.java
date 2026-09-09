package com.habitat.habitat.subscription;

import com.habitat.habitat.organization.Organization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationFeatureRepository extends JpaRepository<OrganizationFeature, UUID> {

	boolean existsByOrganizationAndFeature(Organization organization, Feature feature);

	boolean existsByOrganization_IdAndFeature_Code(UUID organizationId, String featureCode);

	List<OrganizationFeature> findByOrganization_Id(UUID organizationId);

	void deleteByOrganization(Organization organization);
}
