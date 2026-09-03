package com.habitat.habitat.organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "currencies")
public class Currency {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 3)
	private String code;

	@Column(nullable = false, length = 80)
	private String name;

	protected Currency() {
	}

	public Currency(String code, String name) {
		this.code = code.toUpperCase();
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
