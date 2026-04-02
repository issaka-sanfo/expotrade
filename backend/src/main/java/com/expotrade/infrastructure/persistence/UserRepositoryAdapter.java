package com.expotrade.infrastructure.persistence;

import com.expotrade.domain.model.User;
import com.expotrade.domain.port.out.UserRepository;
import com.expotrade.infrastructure.persistence.entity.UserEntity;
import com.expotrade.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository jpaRepo;
    public UserRepositoryAdapter(JpaUserRepository jpaRepo) { this.jpaRepo = jpaRepo; }

    @Override public User save(User user) { return jpaRepo.save(UserEntity.fromDomain(user)).toDomain(); }
    @Override public Optional<User> findById(UUID id) { return jpaRepo.findById(id).map(UserEntity::toDomain); }
    @Override public Optional<User> findByUsername(String u) { return jpaRepo.findByUsername(u).map(UserEntity::toDomain); }
    @Override public Optional<User> findByEmail(String e) { return jpaRepo.findByEmail(e).map(UserEntity::toDomain); }
    @Override public boolean existsByUsername(String u) { return jpaRepo.existsByUsername(u); }
    @Override public boolean existsByEmail(String e) { return jpaRepo.existsByEmail(e); }
}
