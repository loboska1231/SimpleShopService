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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("users")
public class User implements UserDetails {
	@MongoId
	private String id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String refreshToken;
	private String role;

	public Collection<? extends GrantedAuthority> getAuthorities(){
		if(StringUtils.isNotBlank(role)){
			return Set.of(new SimpleGrantedAuthority(role));
		} else return Collections.emptySet();
	}
	@SneakyThrows
	public Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<>();
		Class<?> obj =  this.getClass();
		for(Field f: obj.getDeclaredFields()){
			f.setAccessible(true);
			if(!f.getName().equals("password")) {
				map.put(f.getName(), f.get(this));
			}
			if(f.getName().equals("role")){
				map.put(f.getName(), this.getAuthorities());
			}
		}
		return map;
	}
}
