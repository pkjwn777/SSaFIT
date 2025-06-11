package com.ssafy.model.dao;

import java.util.List;
import java.util.Map;

import com.ssafy.model.dto.Followship;
import com.ssafy.model.dto.User;

public interface UserDao {
	public abstract User selectByUserId(String userId);
//	public abstract User selectByProvider(String provider, String providerId);
	public abstract User selectOne(int userKey);
    
	public abstract int regist(User user);
	public abstract int isExist(String userId);
	
	int getUserKeyByUserId(String userId);
	public abstract List<Integer> searchFollowing(int userKey);
	public abstract List<Integer> searchFollower(int userKey);
	public abstract int follow(Map<String, Object> request);
	public abstract int update(User newUser);
	public abstract int delete(int userKey);
	public abstract List<User> searchList(String username);
}
