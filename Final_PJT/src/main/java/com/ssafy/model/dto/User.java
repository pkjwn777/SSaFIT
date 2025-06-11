package com.ssafy.model.dto;

public class User {
	private int userKey; /** 전체 유저 중에서의 번호 */
	private String userId;
	private String userPassword;
	private String userEmail;
	private String role;
	
	public User() {
		super();
	}

	public User(int userKey, String userId, String userPassword, String userEmail, String role) {
		super();
		this.userKey = userKey;
		this.userId = userId;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
		this.role = role;

	}

	public int getUserKey() {
		return userKey;
	}

	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

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
		return "User [userKey=" + userKey + ", userId=" + userId + ", userPassword=" + userPassword + ", userEmail="
				+ userEmail + "role=" + role + "]";
	}

	
}
