package com.epotrade.application.service;

import com.epotrade.domain.model.MarketData;
import com.epotrade.domain.port.out.BrokerPort;
import com.epotrade.domain.port.out.EventPublisher;
import com.epotrade.domain.port.out.MarketDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketDataService {

    private static final Logger log = LoggerFactory.getLogger(MarketDataService.class);

    private final Map<String, BrokerPort> brokerPorts;
    private final MarketDataCache marketDataCache;
    private final EventPublisher eventPublisher;
    private final Map<String, Disposable> activeSubscriptions = new ConcurrentHashMap<>();

    public MarketDataService(Map<String, BrokerPort> brokerPorts,
                             MarketDataCache marketDataCache,
                             EventPublisher eventPublisher) {
        this.brokerPorts = brokerPorts;
        this.marketDataCache = marketDataCache;
        this.eventPublisher = eventPublisher;
    }

    public void subscribeToMarketData(String brokerName, List<String> symbols) {
        BrokerPort broker = brokerPorts.get(brokerName);
        if (broker == null) {
            log.error("Broker not found: {}", brokerName);
            return;
        }

        String key = brokerName + ":" + String.join(",", symbols);
        if (activeSubscriptions.containsKey(key)) return;

        Disposable sub = broker.streamMarketData(symbols)
                .doOnNext(data -> {
                    marketDataCache.store(data);
                    eventPublisher.publishMarketDataEvent(data);
                })
                .doOnError(e -> log.error("Market data stream error: {}", e.getMessage()))
                .retry(3)
                .subscribe();

        activeSubscriptions.put(key, sub);
        log.info("Subscribed to market data: {}", key);
    }

    public void unsubscribe(String key) {
        Disposable sub = activeSubscriptions.remove(key);
        if (sub != null && !sub.isDisposed()) sub.dispose();
    }

    public Flux<MarketData> getMarketDataStream(String brokerName, List<String> symbols) {
        BrokerPort broker = brokerPorts.get(brokerName);
        if (broker == null) return Flux.error(new IllegalArgumentException("Broker not found: " + brokerName));
        return broker.streamMarketData(symbols);
    }

    public MarketData getLatestMarketData(String symbol) {
        return marketDataCache.getLatest(symbol).orElse(null);
    }
}
