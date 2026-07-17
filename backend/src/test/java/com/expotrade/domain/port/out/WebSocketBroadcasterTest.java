package com.expotrade.domain.port.out;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketBroadcasterTest {

    @Test
    void exposesBroadcastingOperationsWithoutWebSocketTypes() throws NoSuchMethodException {
        assertThat(WebSocketBroadcaster.class.getDeclaredMethod("broadcastToUser", String.class, String.class, String.class, String.class).getReturnType())
                .isEqualTo(void.class);
        assertThat(WebSocketBroadcaster.class.getDeclaredMethod("broadcastToAll", String.class, String.class, String.class).getReturnType())
                .isEqualTo(void.class);
        assertThat(WebSocketBroadcaster.class.getDeclaredMethod("broadcastToSubscribers", String.class, String.class, String.class, String.class).getReturnType())
                .isEqualTo(void.class);
    }
}
