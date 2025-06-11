package com.ssafy.controller;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.JWT.JWTutil;
import com.ssafy.model.dao.RefreshDao;
import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dto.RefreshDTO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {

	private final JWTutil jwtUtil;
	private final RefreshDao refreshDao;
	private final UserDao userDao;

	public ReissueController(JWTutil jwtUtil, RefreshDao refreshDao, UserDao userDao) {

		this.jwtUtil = jwtUtil;
		this.refreshDao = refreshDao;
		this.userDao = userDao;
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

	    //get refresh token
	    String refresh = null;
	    Cookie[] cookies = request.getCookies();
	    for (Cookie cookie : cookies) {

	        if (cookie.getName().equals("refresh")) {

	            refresh = cookie.getValue();
	        }
	    }

	    if (refresh == null) {

	        //response status code
	        return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
	    }

	    //expired check
	    try {
	        jwtUtil.isExpired(refresh);
	    } catch (ExpiredJwtException e) {

	        //response status code
	        return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
	    }

	    // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
	    String category = jwtUtil.getCategory(refresh);

	    if (!category.equals("refresh")) {

	        //response status code
	        return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
	    }
	    
	    //DB에 저장되어 있는지 확인
		Boolean isExist = refreshDao.existsByRefresh(refresh);
		if (!isExist) {
			
			   //response body
			   return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
		}

	    String username = jwtUtil.getUsername(refresh);
	    String role = jwtUtil.getRole(refresh);
	    int userKey = userDao.getUserKeyByUserId(username);

	    //make new JWT
	    String newAccess = jwtUtil.createJwt("access", username,userKey, role, 600000L);
	    String newRefresh = jwtUtil.createJwt("refresh", username, userKey, role, 86400000L);
	    
	    //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
		refreshDao.deleteByRefresh(refresh);
		addRefreshEntity(username, newRefresh, 86400000L);
			
	    //response
	    response.setHeader("access", newAccess);
	    response.addCookie(createCookie("refresh", newRefresh));

	    return new ResponseEntity<>(HttpStatus.OK);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		// cookie.setSecure(true);
		// cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
	
	private void addRefreshEntity(String username, String refresh, Long expiredMs) {

	    Date date = new Date(System.currentTimeMillis() + expiredMs);

	    RefreshDTO refreshDto = new RefreshDTO();
	    refreshDto.setUsername(username);
	    refreshDto.setRefresh(refresh);
	    refreshDto.setExpiration(date.toString());

	    refreshDao.insertRefreshToken(refreshDto);
	}
}
