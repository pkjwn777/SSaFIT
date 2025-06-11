package com.ssafy.model.dto;

public class JoinDTO {
	private String userId;
	private String userPassword;
	private String userEmail;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	@Override
	public String toString() {
		return "JoinDTO [userId=" + userId + ", userPassword=" + userPassword + ", userEmail=" + userEmail + "]";
	}
	
	
	
}
