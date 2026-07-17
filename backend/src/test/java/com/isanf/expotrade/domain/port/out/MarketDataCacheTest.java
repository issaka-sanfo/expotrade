package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.MarketData;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MarketDataCacheTest {

    @Test
    void exposesMarketDataLookupOperations() throws NoSuchMethodException {
        assertThat(MarketDataCache.class.getDeclaredMethod("store", MarketData.class).getReturnType()).isEqualTo(void.class);
        assertThat(MarketDataCache.class.getDeclaredMethod("getLatest", String.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(MarketDataCache.class.getDeclaredMethod("getHistory", String.class, int.class).getReturnType()).isEqualTo(List.class);
    }
}
