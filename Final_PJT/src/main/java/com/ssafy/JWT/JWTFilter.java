package com.ssafy.JWT;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.model.dto.CustomUserDetails;
import com.ssafy.model.dto.User;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{

	private final JWTutil jwtUtil; //검증하는 메서드 사용하려면 있어야 함
	public JWTFilter(JWTutil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 헤더에서 access키에 담긴 토큰을 꺼냄
		String accessToken = request.getHeader("access");

		// 토큰이 없다면 다음 필터로 넘김
		if (accessToken == null) {

		    filterChain.doFilter(request, response);

		    return;
		}
		
		// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
		try {
		    jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {

		    //response body
		    PrintWriter writer = response.getWriter();
		    writer.print("access token expired");

		    //response status code
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    return;
		}

		// 토큰이 access인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(accessToken);

		if (!category.equals("access")) {

		    //response body
		    PrintWriter writer = response.getWriter();
		    writer.print("invalid access token");

		    //response status code
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    return;
		}

		// username, role 값을 획득
		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);

		User user = new User();
		user.setUserId(username);
		user.setRole(role);
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
