package org.project.orderservice.models;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Document("users")
public class User {
	@MongoId
	private String id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String role;

	public Collection<? extends GrantedAuthority> getAuthorities(){
		if(StringUtils.isNotBlank(role)){
			return Set.of(new SimpleGrantedAuthority(role));
		} else return Collections.emptySet();
	}
}
