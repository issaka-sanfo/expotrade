package com.expotrade.domain.port.in;

import com.expotrade.domain.model.Signal;
import com.expotrade.domain.model.StrategyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradingStrategyTest {

    @Test
    void exposesSignalGenerationWithoutAdapterDependencies() throws NoSuchMethodException {
        assertThat(TradingStrategy.class.getDeclaredMethod("getId").getReturnType()).isEqualTo(String.class);
        assertThat(TradingStrategy.class.getDeclaredMethod("getName").getReturnType()).isEqualTo(String.class);
        assertThat(TradingStrategy.class.getDeclaredMethod("supports", String.class).getReturnType()).isEqualTo(boolean.class);
        assertThat(TradingStrategy.class.getDeclaredMethod("generateSignal", String.class, List.class, StrategyConfig.class).getReturnType())
                .isEqualTo(Signal.class);
    }
}
