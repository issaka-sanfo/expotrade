package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    @Test
    void exposesUserPersistenceQueries() throws NoSuchMethodException {
        assertThat(UserRepository.class.getDeclaredMethod("save", User.class).getReturnType()).isEqualTo(User.class);
        assertThat(UserRepository.class.getDeclaredMethod("findById", UUID.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(UserRepository.class.getDeclaredMethod("findByUsername", String.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(UserRepository.class.getDeclaredMethod("findByEmail", String.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(UserRepository.class.getDeclaredMethod("existsByUsername", String.class).getReturnType()).isEqualTo(boolean.class);
        assertThat(UserRepository.class.getDeclaredMethod("existsByEmail", String.class).getReturnType()).isEqualTo(boolean.class);
    }
}
