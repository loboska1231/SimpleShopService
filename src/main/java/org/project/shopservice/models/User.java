package org.project.shopservice.models;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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
}
