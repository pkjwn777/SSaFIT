package com.ssafy.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.exception.RegistException;
import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dto.Followship;
import com.ssafy.model.dto.JoinDTO;
import com.ssafy.model.dto.User;

@Service
public class UserServiceImpl implements UserService {
	private final UserDao userDao;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder){
		this.userDao = userDao;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
//
//	@Override
//	public void Auth(String userId, String userPassword) throws AuthException {
//		User user = new User();
//		user.setUserId(userId);
//		user.setUserPassword(userPassword);
//		// repo의 Auth는 불린값 반환, DB가 일치를 확인 했으면 true 아니면 false
//		if (userDao.Auth(user) == 1) {
//			return;
//		} else {
//			throw new AuthException();
//		}
//	}

	@Override
	public void regist(JoinDTO joinDTO) throws RegistException {
	    try {
	        // 중복 검사
	        if (joinDTO.getUserId() != null && userDao.isExist(joinDTO.getUserId()) != 0) {
	            throw new RegistException("해당 아이디는 사용할 수 없습니다. 다른 아이디를 사용해주세요.");

	        }
	        String userId = joinDTO.getUserId();
	        String userEmail = joinDTO.getUserEmail();
	        String userPassword = joinDTO.getUserPassword();
	        User user = new User();

	        user.setUserId(userId);
	        user.setUserEmail(userEmail);
	        user.setUserPassword(bCryptPasswordEncoder.encode(userPassword));
	        userDao.regist(user);	        

	        
	    } catch (Exception e) {
	        // 디버깅을 위해 예외 출력 추가
	        e.printStackTrace();
	        throw new RegistException("회원가입 중 오류가 발생했습니다.");
	    }
	}

	@Override
	public User selectOne(int userKey) {
		return userDao.selectOne(userKey);
	}

	////	@Override
////	public List<User> selectFollower(Followship relation) {
////		return userDao.selectFollower(relation);
////	}
//
////	@Override
////	public List<User> selectFollowing(Followship relation) {
////		return userDao.selectFollowing(relation);
////	}
//
//	@Override
//	public int updateUser(User user) {
//		return userDao.updateUser(user);
//	}
//
////	@Override
////	public int deleteUser(int userKey) {
////		return userDao.deleteUser(userKey);
////	}
//
	@Override
	public User selectByUserId(String userId) {
	    return userDao.selectByUserId(userId);
	}
//	
//	@Override
//	public User selectByProvider(String provider, String providerId) {
//	    return userDao.selectByProvider(provider, providerId);
//	}
//
//	@Override
//	public User selectByEmail(String email) {
//	    return userDao.selectByEmail(email);
//	}
	
	public int getUserKeyByUserId(String userId)
	{
		return userDao.getUserKeyByUserId(userId);
	}

	@Override
	public List<Integer> searchFollowing(int userKey) {
		return userDao.searchFollowing(userKey);
	}
	
	@Override
	public List<Integer> searchFollower(int userKey) {
		return userDao.searchFollower(userKey);
	}

	@Override
	public boolean follow(Map<String, Object> request) {
		return userDao.follow(request) == 1;
	}

	@Override
	public int update(User newUser) {
		return userDao.update(newUser);
	}

	@Override
	public boolean delete(int userKey) {
		return userDao.delete(userKey) == 1;
	}

	@Override
	public List<User> searchList(String username) {
		return userDao.searchList(username);
	}
}
