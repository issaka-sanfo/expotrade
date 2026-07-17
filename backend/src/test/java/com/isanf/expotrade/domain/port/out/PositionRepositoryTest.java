package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PositionRepositoryTest {

    @Test
    void exposesPositionPersistenceQueries() throws NoSuchMethodException {
        assertThat(PositionRepository.class.getDeclaredMethod("save", Position.class).getReturnType()).isEqualTo(Position.class);
        assertThat(PositionRepository.class.getDeclaredMethod("findById", UUID.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(PositionRepository.class.getDeclaredMethod("findByUserId", UUID.class).getReturnType()).isEqualTo(List.class);
        assertThat(PositionRepository.class.getDeclaredMethod("findByUserIdAndSymbol", UUID.class, String.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(PositionRepository.class.getDeclaredMethod("deleteById", UUID.class).getReturnType()).isEqualTo(void.class);
    }
}
