package com.inpulse.auth_backend.infra.persistence.mapper;

import com.inpulse.auth_backend.core.user.model.User;
import com.inpulse.auth_backend.infra.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}
