package com.cineproj.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {
	
    private UUID id;
    private String username;
    private String password;
    private Boolean isCinema;
    private Boolean isAdmin;
    private String email;
    private String created_at;
	
	public Boolean isCinema() {
		return isCinema;
	}

	public void setCinema(Boolean isCinema) {
		this.isCinema = isCinema;
	}

	public Boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User() {}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}
