package com.expotrade.domain.port.out;

import com.expotrade.domain.model.Trade;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TradeRepositoryTest {

    @Test
    void exposesTradePersistenceQueries() throws NoSuchMethodException {
        assertThat(TradeRepository.class.getDeclaredMethod("save", Trade.class).getReturnType()).isEqualTo(Trade.class);
        assertThat(TradeRepository.class.getDeclaredMethod("findById", UUID.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(TradeRepository.class.getDeclaredMethod("findByUserId", UUID.class).getReturnType()).isEqualTo(List.class);
        assertThat(TradeRepository.class.getDeclaredMethod("findByOrderId", UUID.class).getReturnType()).isEqualTo(List.class);
        assertThat(TradeRepository.class.getDeclaredMethod("findBySymbolAndDateRange", String.class, Instant.class, Instant.class).getReturnType())
                .isEqualTo(List.class);
    }
}
