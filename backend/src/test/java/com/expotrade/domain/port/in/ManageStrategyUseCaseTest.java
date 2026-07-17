package com.expotrade.domain.port.in;

import com.expotrade.domain.model.StrategyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ManageStrategyUseCaseTest {

    @Test
    void exposesStrategyLifecycleOperations() throws NoSuchMethodException {
        assertThat(ManageStrategyUseCase.class.getDeclaredMethod("enableStrategy", String.class, UUID.class).getReturnType())
                .isEqualTo(StrategyConfig.class);
        assertThat(ManageStrategyUseCase.class.getDeclaredMethod("disableStrategy", String.class, UUID.class).getReturnType())
                .isEqualTo(StrategyConfig.class);
        assertThat(ManageStrategyUseCase.class.getDeclaredMethod("getStrategies", UUID.class).getReturnType())
                .isEqualTo(List.class);
        assertThat(ManageStrategyUseCase.class.getDeclaredMethod("createStrategy", StrategyConfig.class).getReturnType())
                .isEqualTo(StrategyConfig.class);
    }
}
