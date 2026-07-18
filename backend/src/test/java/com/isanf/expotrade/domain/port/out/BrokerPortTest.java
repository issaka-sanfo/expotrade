package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.BrokerCredentials;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerPortTest {

    @Test
    void exposesReactiveBrokerOperations() throws NoSuchMethodException {
        assertThat(BrokerPort.class.getDeclaredMethod("placeOrder", Order.class).getReturnType()).isEqualTo(Mono.class);
        assertThat(BrokerPort.class.getDeclaredMethod("cancelOrder", String.class).getReturnType()).isEqualTo(Mono.class);
        assertThat(BrokerPort.class.getDeclaredMethod("getPositions").getReturnType()).isEqualTo(Mono.class);
        assertThat(BrokerPort.class.getDeclaredMethod("getMarketData", String.class).getReturnType()).isEqualTo(Mono.class);
        assertThat(BrokerPort.class.getDeclaredMethod("streamMarketData", List.class).getReturnType()).isEqualTo(Flux.class);
        assertThat(BrokerPort.class.getDeclaredMethod("getOrderStatus", String.class).getReturnType()).isEqualTo(Mono.class);
        assertThat(BrokerPort.class.getDeclaredMethod("verifyCredentials", BrokerCredentials.class).getReturnType()).isEqualTo(Mono.class);
    }
}
