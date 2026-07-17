package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.port.out.BrokerPort;
import com.isanf.expotrade.domain.port.out.EventPublisher;
import com.isanf.expotrade.domain.port.out.MarketDataCache;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketDataServiceTest {

    @Test
    void getMarketDataStreamReturnsBrokerStream() {
        BrokerPort broker = mock(BrokerPort.class);
        MarketData data = marketData("AAPL", 100);
        when(broker.streamMarketData(List.of("AAPL"))).thenReturn(Flux.just(data));
        MarketDataService service = service(Map.of("IBKR", broker), mock(MarketDataCache.class));

        List<MarketData> emitted = service.getMarketDataStream("IBKR", List.of("AAPL")).collectList().block();

        assertEquals(List.of(data), emitted);
    }

    @Test
    void getMarketDataStreamFailsForUnknownBroker() {
        MarketDataService service = service(Map.of(), mock(MarketDataCache.class));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.getMarketDataStream("UNKNOWN", List.of("AAPL")).blockFirst());

        assertEquals("Broker not found: UNKNOWN", error.getMessage());
    }

    @Test
    void getLatestMarketDataReturnsCachedDataOrNull() {
        MarketDataCache cache = mock(MarketDataCache.class);
        MarketData data = marketData("AAPL", 100);
        when(cache.getLatest("AAPL")).thenReturn(Optional.of(data));
        when(cache.getLatest("MSFT")).thenReturn(Optional.empty());
        MarketDataService service = service(Map.of(), cache);

        assertSame(data, service.getLatestMarketData("AAPL"));
        assertNull(service.getLatestMarketData("MSFT"));
    }

    @Test
    void subscribeStoresAndPublishesStreamData() {
        BrokerPort broker = mock(BrokerPort.class);
        MarketDataCache cache = mock(MarketDataCache.class);
        EventPublisher eventPublisher = mock(EventPublisher.class);
        MarketData data = marketData("AAPL", 100);
        when(broker.streamMarketData(List.of("AAPL"))).thenReturn(Flux.just(data));
        MarketDataService service = new MarketDataService(Map.of("IBKR", broker), cache, eventPublisher);

        service.subscribeToMarketData("IBKR", List.of("AAPL"));

        verify(cache).store(data);
        verify(eventPublisher).publishMarketDataEvent(data);
    }

    private MarketDataService service(Map<String, BrokerPort> brokers, MarketDataCache cache) {
        return new MarketDataService(brokers, cache, mock(EventPublisher.class));
    }

    private MarketData marketData(String symbol, int last) {
        BigDecimal price = BigDecimal.valueOf(last);
        return new MarketData(symbol, price, price, price, BigDecimal.TEN,
                price, price, price, price, Instant.now());
    }
}
