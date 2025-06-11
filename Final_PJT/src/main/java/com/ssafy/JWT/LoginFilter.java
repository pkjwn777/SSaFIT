package com.ssafy.JWT;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ssafy.model.dao.RefreshDao;
import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dto.CustomUserDetails;
import com.ssafy.model.dto.RefreshDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class LoginFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	//JwtUtil 주입
	private final JWTutil jwtUtil;
	private final RefreshDao refreshDao;
	private final UserDao userDao;
	
	public LoginFilter(AuthenticationManager authenticationManager, JWTutil jwtUtil, RefreshDao refreshDao, UserDao userDao) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshDao = refreshDao;
		this.userDao = userDao;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		System.out.println(password);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
		
		return authenticationManager.authenticate(authToken);
	}
	//인증을 거쳐 성공하면, JWT 발급
	
	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);
		return cookie;
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		//특정 유저 가져오기
		String username = authentication.getName();
		System.out.println(username);
		//역할을 뽑아내기 위해서는 컬렉션에서 인증 객체를 뽑아낸 다음 반복해서 role을 탐색
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        int userKey = userDao.getUserKeyByUserId(username);
        System.out.println(userKey);
        //마지막 만료 시간은 임의로 넣음
        String access = jwtUtil.createJwt("access",  username, userKey, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh",  username, userKey, role, 86400000L);
        
      //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);
        
        //발급한 토큰을 응답의 헤더에 붙인다. 키는 Authorization(인증), value는 인증방식(접두사)" " + 실제 토큰
        //Http 인증방식이기 때문에 그 정의에 따라 헤더 형태를 다듬는 것, " "  필수
        response.setHeader("access", access); // access 토큰을 헤더에 담기
        response.setHeader("Access-Control-Expose-Headers", "access"); // 🚨 CORS 허용을 위한 노출 설정
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException faild) {
		System.out.println("failed");
		response.setStatus(401);
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
