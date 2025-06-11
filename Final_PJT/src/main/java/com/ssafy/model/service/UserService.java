package com.ssafy.model.service;

import java.util.List;
import java.util.Map;

import com.ssafy.exception.RegistException;
import com.ssafy.model.dto.Followship;
import com.ssafy.model.dto.JoinDTO;
import com.ssafy.model.dto.User;

public interface UserService {
//	public abstract void Auth(String userId, String userPassword) throws AuthException;
	public abstract void regist(JoinDTO joinDTO) throws RegistException;
	public abstract User selectOne(int userKey);
	//selectAll
//	public abstract List<User> selectFollower(Followship relation);
//	public abstract List<User> selectFollowing(Followship relation);
//	public abstract int updateUser(User user);
//	public abstract int deleteUser(int userKey);
	
//	// 소셜 로그인 & 중복확인용 추가
	public abstract User selectByUserId(String userId);
//	public abstract User selectByProvider(String provider, String providerId);
//	public abstract User selectByEmail(String email);
	
	int getUserKeyByUserId(String userId);
	public abstract List<Integer> searchFollowing(int userKey);
	public abstract List<Integer> searchFollower(int userKey);
	public abstract boolean follow(Map<String, Object> request);
	public abstract int update(User newUser);
	public abstract boolean delete(int userKey);
	public abstract List<User> searchList(String username);
}
