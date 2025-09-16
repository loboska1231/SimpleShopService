package org.project.orderservice.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtils { //### 5 ###

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access-token.ttl-millis}")
	private long accessTokenTtlMillis;
	@Value("${jwt.refresh-token.ttl-millis}")
	private long refreshTokenTtlMillis;
	private Key key;

	private JwtParser jwtParser;

	@PostConstruct
	public void setKey(){
		key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
	}

	private String generateToken(String username, long ttlMillis, Map<String, Object> claims) {
		return Jwts
				.builder()
				.signWith(key, SignatureAlgorithm.HS256)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + ttlMillis))
				.setSubject(username)
				.setClaims(claims)
				.compact();
	}

	public String generateAccessToken(UserDetails userDetails){
		List<String> roles = userDetails
				.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		return generateToken(userDetails.getUsername(),accessTokenTtlMillis, Map.of("roles", roles));
	}

	public String generateRefreshToken(UserDetails userDetails){
		return generateToken(userDetails.getUsername(),refreshTokenTtlMillis, Collections.emptyMap());
	}

	public boolean isExpired(String token){
		Date expireAt = extractFromToken(token, Claims::getExpiration);
		return expireAt.before(new Date());
	}

	public <T> T extractFromToken(String token, Function<Claims, T> extractor){
		Claims claims = jwtParser.parseClaimsJwt(token).getBody();
		return extractor.apply(claims);
	}

	public String extractUsername(String token){
		return extractFromToken(token, Claims::getSubject);
	}
}
