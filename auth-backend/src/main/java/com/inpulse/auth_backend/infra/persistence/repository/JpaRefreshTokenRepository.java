package com.inpulse.auth_backend.infra.persistence.repository;

import com.inpulse.auth_backend.infra.persistence.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Query("select rt from RefreshTokenEntity rt join fetch rt.user where rt.token = :token")
    Optional<RefreshTokenEntity> findByToken(@Param("token") String token);

    void deleteByUser_Id(Long userId);
}
