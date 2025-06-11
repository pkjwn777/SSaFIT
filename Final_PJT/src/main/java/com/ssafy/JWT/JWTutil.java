package com.ssafy.JWT;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTutil {
	// JWT(헤더 / 페이로더 / 시그니처)
	private SecretKey secretKey;

	// 저장된 개발자 지정 비밀번호를 Value로 가져옴 => Jwt에서 객체 타입으로 만들어 저장, 이것을 암호화 하는 과정
	public JWTutil(@Value("${spring.jwt.secret}") String secret) {
		// 환경 변수로 받아온 우리가 지정한 JWT 암호(발급처 확인용 우리가 사용할 비밀키)
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
				Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	// 토큰 종류 체크하는 메소드
	public String getCategory(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category",
				String.class);
	}

	// username 확인 메소드(검증 - 토큰에서 뽑아냄)
	public String getUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username",
				String.class);
	}

	// 역할 확인(검증 - 토큰에서 뽑아냄)
	public String getRole(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",
				String.class);
	}

	// 역할 확인(검증 - 토큰에서 뽑아냄)
	public String getUserKey(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userKey",
				String.class);
	}

	// 만료일 확인 메소돟(검증 - 토큰에서 뽑아냄)
	public Boolean isExpired(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
				.before(new Date());
	}

	// Jwt 구현 메소드(토큰 생성 메소드)
	public String createJwt(String category, String username, int userKey, String role, Long expiredMs) {
		return Jwts.builder()
				.claim("category", category)
				.claim("username", username)
				.claim("userKey", userKey)// 토큰에 넣을
				.claim("role", role) // 두개 전부 페이로더에 들어감
				.issuedAt(new Date(System.currentTimeMillis())) // 발행 시간
				.expiration(new Date(System.currentTimeMillis() + expiredMs)) // 발행 시간으로부터 우리가 인지로 받은 살아있는 시간까지 유효
				.signWith(secretKey) // 암호화 과정
				.compact(); // 압출해서 리턴
	}
}
