package com.habitat.property;

import com.habitat.common.domain.BusinessRuleViolationException;
import com.habitat.common.domain.ResourceNotFoundException;
import com.habitat.identity.RoleName;
import com.habitat.identity.TenantContext;
import com.habitat.organization.Organization;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyManagementService {

	private final OwnerRepository ownerRepository;
	private final PropertyRepository propertyRepository;
	private final UnitRepository unitRepository;
	private final TenantContext tenantContext;

	public PropertyManagementService(
			OwnerRepository ownerRepository,
			PropertyRepository propertyRepository,
			UnitRepository unitRepository,
			TenantContext tenantContext) {
		this.ownerRepository = ownerRepository;
		this.propertyRepository = propertyRepository;
		this.unitRepository = unitRepository;
		this.tenantContext = tenantContext;
	}

	@Transactional
	public OwnerResponse createOwner(CreateOwnerRequest request) {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		Organization organization = tenantContext.currentOrganization();
		if (ownerRepository.existsByOrganization_IdAndEmailIgnoreCase(organization.getId(), request.email())) {
			throw new BusinessRuleViolationException("An owner with this email already exists in this organization");
		}

		Owner owner = ownerRepository.save(new Owner(
				organization,
				request.fullName(),
				request.email(),
				request.phoneNumber()));
		return OwnerResponse.from(owner);
	}

	@Transactional
	public PropertyResponse createProperty(CreatePropertyRequest request) {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		Organization organization = tenantContext.currentOrganization();
		Owner owner = request.ownerId() == null ? null : owner(request.ownerId(), organization.getId());
		Property property = propertyRepository.save(new Property(
				organization,
				owner,
				request.name(),
				request.addressLine1(),
				request.city(),
				request.region()));
		return PropertyResponse.from(property);
	}

	@Transactional(readOnly = true)
	public List<PropertyResponse> listProperties() {
		return propertyRepository.findByOrganization_IdOrderByNameAsc(tenantContext.currentOrganizationId()).stream()
				.map(PropertyResponse::from)
				.toList();
	}

	@Transactional
	public UnitResponse createUnit(UUID propertyId, CreateUnitRequest request) {
		tenantContext.requireRole(RoleName.ORG_ADMIN);
		Organization organization = tenantContext.currentOrganization();
		Property property = property(propertyId, organization.getId());
		Unit unit = unitRepository.save(new Unit(organization, property, request.name(), request.monthlyRent()));
		return UnitResponse.from(unit);
	}

	@Transactional(readOnly = true)
	public List<UnitResponse> listUnits(UUID propertyId) {
		UUID organizationId = tenantContext.currentOrganizationId();
		property(propertyId, organizationId);
		return unitRepository.findByProperty_IdAndOrganization_IdOrderByNameAsc(propertyId, organizationId).stream()
				.map(UnitResponse::from)
				.toList();
	}

	private Owner owner(UUID ownerId, UUID organizationId) {
		return ownerRepository.findByIdAndOrganization_Id(ownerId, organizationId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner does not exist in this organization"));
	}

	private Property property(UUID propertyId, UUID organizationId) {
		return propertyRepository.findByIdAndOrganization_Id(propertyId, organizationId)
				.orElseThrow(() -> new ResourceNotFoundException("Property does not exist in this organization"));
	}
}
