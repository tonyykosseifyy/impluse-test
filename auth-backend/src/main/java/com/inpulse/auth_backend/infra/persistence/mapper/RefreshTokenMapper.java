package com.inpulse.auth_backend.infra.persistence.mapper;

import com.inpulse.auth_backend.core.auth.model.RefreshToken;
import com.inpulse.auth_backend.infra.persistence.entity.RefreshTokenEntity;
import com.inpulse.auth_backend.infra.persistence.entity.UserEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "userId", source = "user.id")
    RefreshToken toDomain(RefreshTokenEntity entity);

    @Mapping(target = "user", expression = "java(userEntity)")
    RefreshTokenEntity toEntity(RefreshToken domain, @Context UserEntity userEntity);
}
