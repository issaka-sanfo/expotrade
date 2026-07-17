package com.isanf.expotrade.domain.port.in;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetPortfolioUseCaseTest {

    @Test
    void exposesPortfolioLookupByUser() throws NoSuchMethodException {
        Method getPortfolio = GetPortfolioUseCase.class.getDeclaredMethod("getPortfolio", UUID.class);

        assertThat(getPortfolio.getReturnType()).isEqualTo(Mono.class);
    }
}
