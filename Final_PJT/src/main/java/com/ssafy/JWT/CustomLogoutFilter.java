package com.ssafy.JWT;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.ssafy.model.dao.RefreshDao;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean{
	private final JWTutil jwtUtil;
    private final RefreshDao refreshDao;

    public CustomLogoutFilter(JWTutil jwtUtil, RefreshDao refreshDao) {

        this.jwtUtil = jwtUtil;
        this.refreshDao = refreshDao;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //로그아웃 요청만을 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
        	//로그아웃 아니면 넘어가기
            filterChain.doFilter(request, response);
            return;
        }
        //로그아웃이 포스트가 아닐 때 넘어가기
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }
        
        
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        //refresh null check - 리프레쉬 토큰 없으면 400대 응답
        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //토큰이 잘 있으면 만료를 확인, 만료라면 이미 로그아웃됨 + 만료가 아니라면 리프레쉬 토큰 삭제(로그아웃 처리)
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시) - 리프레쉬 토큰 아니라면 400대 응답
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshDao.existsByRefresh(refresh);
        if (!isExist) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshDao.deleteByRefresh(refresh);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
