package com.ssafy.model.dto;

public class Followship {
	private int follower;
	private int following;

	public Followship() {}
	
	public Followship(int follower, int following) {
		super();
		this.follower = follower;
		this.following = following;
	}
	public int getFollower() {
		return follower;
	}
	public void setFollower(int follower) {
		this.follower = follower;
	}
	public int getFollowing() {
		return following;
	}
	public void setFollowing(int following) {
		this.following = following;
	}
	@Override
	public String toString() {
		return "Followship [follower=" + follower + ", following=" + following + "]";
	}
}
