package org.project.shopservice.services;


import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.models.User;
import org.project.shopservice.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	private String extractFullName(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isNotBlank(username)){
			User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
			return user.getFirstName()+" "+user.getLastName();
		}
		throw new JwtException("Username is empty");
	}
	private String extractUsername(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isNotBlank(username)){
			if(userRepository.findByEmail(username).isPresent())
				return username;
			else throw new UsernameNotFoundException("User not found");
		}
		throw new JwtException("Username is empty");
	}

	public OrderEntity fillFieldsEmailAndWhose(OrderEntity order){
		order.setEmail(extractUsername());
		order.setWhose(extractFullName());
		return order;
	}

	public boolean canAccess(String email){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)    SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		return username.equals(email) || authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
}
