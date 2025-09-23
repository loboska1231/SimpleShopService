package org.project.shopservice.security.filters;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException { // ### 2 ###
		String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION); // ### 4 ###
		if(StringUtils.isBlank(authHeaderValue) ||  !authHeaderValue.startsWith("Bearer ")){
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeaderValue.substring(7);
		log.info("token :: {}", token);
//		String username = jwtUtil.extractUsername(token);
//		try{
//			if(StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null){
//				UserDetails user = userDetailsService.loadUserByUsername(username);
//				if (jwtUtil.isTokenValid(token, user)) {
//					SecurityContext context = SecurityContextHolder.createEmptyContext();
//					log.info(user.toString());
//					UsernamePasswordAuthenticationToken authentication =
//							new UsernamePasswordAuthenticationToken(
//									user,
//									null,
//									user.getAuthorities()
//							);
//					authentication.setDetails(
//							new WebAuthenticationDetailsSource()
//									.buildDetails(request)
//					);
//					context.setAuthentication(authentication);
//					SecurityContextHolder.setContext(context);
//				}
//			}
//		} catch (Exception e){
//			throw new AuthorizationDeniedException(e.getMessage());
//		} finally {
//			filterChain.doFilter(request, response);
//		}
		if(StringUtils.isBlank(token) ){
			filterChain.doFilter(request, response);
			return;
		}
		try{
			String username = jwtUtil.extractUsername(token);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if(!jwtUtil.isTokenValid(token, userDetails)){
				filterChain.doFilter(request, response);
				return;
			}
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e){
			throw new AuthenticationException(e.getMessage());
		} finally {
			filterChain.doFilter(request, response);
		}
	}
}
