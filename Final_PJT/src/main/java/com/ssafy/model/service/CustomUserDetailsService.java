package com.ssafy.model.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dto.CustomUserDetails;
import com.ssafy.model.dto.User;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	private final UserDao userDao;
	public CustomUserDetailsService(UserDao userDao){
		this.userDao = userDao;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userDao.selectByUserId(username);
		if(user != null) {
			return new CustomUserDetails(user);
		}
		return null;
	}

}
