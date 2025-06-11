package com.ssafy.model.dto;

public class RefreshDTO {
	private Long id;
    private String username;
    private String refresh;
    private String expiration;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRefresh() {
		return refresh;
	}
	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	@Override
	public String toString() {
		return "RefreshDTO [id=" + id + ", username=" + username + ", refresh=" + refresh + ", expiration=" + expiration
				+ "]";
	}
}
