package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest {

    @Test
    void exposesOrderPersistenceQueries() throws NoSuchMethodException {
        assertThat(OrderRepository.class.getDeclaredMethod("save", Order.class).getReturnType()).isEqualTo(Order.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findById", UUID.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findByUserId", UUID.class).getReturnType()).isEqualTo(List.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findByStatus", OrderStatus.class).getReturnType()).isEqualTo(List.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findByStrategyId", String.class).getReturnType()).isEqualTo(List.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findByUserIdAndStrategyId", UUID.class, String.class).getReturnType()).isEqualTo(List.class);
        assertThat(OrderRepository.class.getDeclaredMethod("findByExternalOrderId", String.class).getReturnType()).isEqualTo(Optional.class);
    }
}
