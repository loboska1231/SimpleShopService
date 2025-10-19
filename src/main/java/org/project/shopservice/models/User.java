package org.project.shopservice.models;

import io.micrometer.common.util.StringUtils;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("users")
public class User implements UserDetails {
	@MongoId
	private String id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String refreshToken;
	private Set<String> roles;

	public Collection<? extends GrantedAuthority>   getAuthorities(){
		if(!roles.isEmpty()){
			return roles.stream().map(role-> new SimpleGrantedAuthority("ROLE_%s".formatted(role))).collect(Collectors.toSet());
		} else return Collections.emptySet();
	}

	public Map<String, Object> getRoles(){
		List<String> roles = this.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		return Map.of("roles",roles);
	}

	@Override
	public String getUsername() {
		return email;
	}

	@SneakyThrows
	public Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<>();
		Class<?> obj =  this.getClass();
		for(Field f: obj.getDeclaredFields()){
			f.setAccessible(true);
			String field = f.getName();
			if(field.equals("refreshToken")){
				continue;
			}
			if(!field.equals("password")) {
				map.put(field, f.get(this));
			}
			if(field.equals("role")){
				map.put(field, this.getRoles());
			}
		}
		return map;
	}
}
