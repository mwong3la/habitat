package com.habitat.habitat.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "permissions")
public class Permission {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 80)
	private String code;

	protected Permission() {
	}

	public Permission(String code) {
		this.code = code;
	}

	public UUID getId() {
		return id;
	}

	public String getCode() {
		return code;
	}
}
