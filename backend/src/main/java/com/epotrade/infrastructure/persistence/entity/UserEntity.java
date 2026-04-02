package com.epotrade.infrastructure.persistence.entity;

import com.epotrade.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String passwordHash;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role") private Set<String> roles;
    private boolean enabled;
    @Column(nullable = false) private Instant createdAt;

    protected UserEntity() {}

    public static UserEntity fromDomain(User u) {
        UserEntity e = new UserEntity();
        e.id = u.id(); e.username = u.username(); e.email = u.email();
        e.passwordHash = u.passwordHash(); e.roles = u.roles();
        e.enabled = u.enabled(); e.createdAt = u.createdAt();
        return e;
    }

    public User toDomain() {
        return new User(id, username, email, passwordHash, roles, enabled, createdAt);
    }
}
