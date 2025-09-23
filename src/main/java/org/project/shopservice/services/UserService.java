package org.project.shopservice.services;


import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.models.User;
import org.project.shopservice.repository.UserRepository;
import org.project.shopservice.security.utils.JwtUtil;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	private String extractFullName(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isNotBlank(username)){
			User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
			return user.getFirstName()+" "+user.getLastName();
		}
		return null;
	}
	private String extractUsername(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(StringUtils.isNotBlank(username) && userRepository.findByUsername(username).isPresent()){
			return username;
		}
		return null;
	}

	public OrderEntity fillFieldsUsernameAndWhose(OrderEntity order){
		String username = extractUsername();
		String whose = extractFullName();
		order.setUsername(username);
		order.setWhose(whose);
		return order;
	}
}
