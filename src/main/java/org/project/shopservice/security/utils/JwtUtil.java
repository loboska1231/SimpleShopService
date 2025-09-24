package org.project.shopservice.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.project.shopservice.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret-key}")
	private  String secret;
	@Value("${jwt.access-token.ttl-millis}")
	private long accessTokenTtlMillis;
	@Value("${jwt.refresh-token.ttl-millis}")
	private long refreshTokenTtlMillis;
	private Key key;
	private JwtParser jwtParser;

	@PostConstruct
	public void setUpKey() {
		key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
	}

	private String generateToken(String username, long ttlMillis, Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + ttlMillis))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public String generateAccessToken(User user){
		return generateToken(user.getUsername(),accessTokenTtlMillis, user.toMap());
	}

	public String generateRefreshToken(User user){
		return generateToken(user.getUsername(),refreshTokenTtlMillis, user.toMap());
	}
	private boolean isExpired(String token){
		Date expireAt = extractFromToken(token, Claims::getExpiration);
		return !expireAt.after(new Date());
	}
	private <T> T extractFromToken(String token, Function<Claims, T> extractor){
		Claims claims = jwtParser.parseClaimsJws(token).getBody();
		return extractor.apply(claims);
	}
	public String extractUsername(String token){
		return extractFromToken(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = userDetails.getUsername();
		return username.equals(extractUsername(token)) && !isExpired(token);
	}
}
