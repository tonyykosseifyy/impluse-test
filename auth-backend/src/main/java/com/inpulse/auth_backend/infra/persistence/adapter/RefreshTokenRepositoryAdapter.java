package com.inpulse.auth_backend.infra.persistence.adapter;

import com.inpulse.auth_backend.core.auth.model.RefreshToken;
import com.inpulse.auth_backend.core.auth.port.RefreshTokenRepositoryPort;
import com.inpulse.auth_backend.infra.persistence.entity.UserEntity;
import com.inpulse.auth_backend.infra.persistence.mapper.RefreshTokenMapper;
import com.inpulse.auth_backend.infra.persistence.repository.JpaRefreshTokenRepository;
import com.inpulse.auth_backend.infra.persistence.repository.JpaUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final JpaUserRepository jpaUserRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    public RefreshTokenRepositoryAdapter(
            JpaRefreshTokenRepository jpaRefreshTokenRepository,
            JpaUserRepository jpaUserRepository,
            RefreshTokenMapper refreshTokenMapper
    ) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        UserEntity userEntity = jpaUserRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for refresh token"));
        return refreshTokenMapper.toDomain(
                jpaRefreshTokenRepository.save(refreshTokenMapper.toEntity(refreshToken, userEntity))
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token).map(refreshTokenMapper::toDomain);
    }

    @Override
    public void deleteByUserId(Long userId) {
        jpaRefreshTokenRepository.deleteByUser_Id(userId);
    }
}
