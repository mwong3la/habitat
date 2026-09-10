package com.habitat.identity;

import com.habitat.organization.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class AppUser {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Column(nullable = false, length = 160)
	private String fullName;

	@Column(nullable = false, unique = true, length = 180)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(nullable = false, updatable = false)
	private Instant createdAt = Instant.now();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "app_user_roles",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	protected AppUser() {
	}

	public AppUser(Organization organization, String fullName, String email, String passwordHash) {
		this.organization = organization;
		this.fullName = fullName;
		this.email = email.toLowerCase();
		this.passwordHash = passwordHash;
	}

	public UUID getId() {
		return id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public boolean hasRole(RoleName roleName) {
		return roles.stream().anyMatch(role -> role.getName() == roleName);
	}

	public void addRole(Role role) {
		roles.add(role);
	}
}
