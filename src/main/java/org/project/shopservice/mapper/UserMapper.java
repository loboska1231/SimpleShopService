package org.project.shopservice.mapper;

import org.mapstruct.Mapper;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.models.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {
	User toModel(UserRegistrationDto dto);
}
