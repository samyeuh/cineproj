package com.cineproj.request;

public class UserRequest {
	
    private String id;
    private String username;
    private Boolean isCinema;
    private Boolean isAdmin;
    private String email;
    private String created_before;
    private String created_after;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
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
	public String getCreatedBefore() {
		return created_before;
	}
	public void setCreatedBefore(String created_before) {
		this.created_before = created_before;
	}
	public String getCreatedAfter() {
		return created_after;
	}
	public void setCreatedAfter(String created_after) {
		this.created_after = created_after;
	}
    
    
}
