package com.isanf.expotrade.domain.port.in;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CancelOrderUseCaseTest {

    @Test
    void exposesOrderCancellationByIdentifier() throws NoSuchMethodException {
        Method cancelOrder = CancelOrderUseCase.class.getDeclaredMethod("cancelOrder", UUID.class);

        assertThat(cancelOrder.getReturnType()).isEqualTo(Mono.class);
    }
}
