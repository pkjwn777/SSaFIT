package com.ssafy.config;

import java.util.List;

import org.springframework.web.cors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ssafy.JWT.CustomLogoutFilter;
import com.ssafy.JWT.JWTFilter;
import com.ssafy.JWT.JWTutil;
import com.ssafy.JWT.LoginFilter;
import com.ssafy.model.dao.RefreshDao;
import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dto.Video;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//   private final CustomOAuth2UserService oAuth2UserService;
//   private final OAuth2SuccessHandler oAuth2SuccessHandler;

//   public SecurityConfig(CustomOAuth2UserService oAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) {
//  	  this.oAuth2UserService = oAuth2UserService;
//        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
//   }

//	AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTutil jwtUtil;
	private final RefreshDao refreshDao;
	private final UserDao userDao;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTutil jwtUtil,
			RefreshDao refreshDao, UserDao userDao) {

		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.refreshDao = refreshDao;
		this.userDao = userDao;
	}

	// AuthenticationManager Bean 등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 또는 커스터마이징 하고 싶다면
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5176/"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CORS 허용 설정 추가
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		// csrf 끄기
		http.csrf((auth) -> auth.disable());

		// Form 로그인 방식 disable
		http.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());

		// 경로 별 인가 작업
		http.authorizeHttpRequests(
				(auth) -> auth.requestMatchers("/", "/login", "/user/regist", "/video/**", "/reissue", "/api/**")
						.permitAll().anyRequest().authenticated());

		// JWT 필터 등록
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

		LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
				refreshDao, userDao);
		loginFilter.setFilterProcessesUrl("/login"); // ⭐ 이거 꼭 추가해야 함!

		http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		// 로그아웃 필터
		http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshDao), LogoutFilter.class);

		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

}