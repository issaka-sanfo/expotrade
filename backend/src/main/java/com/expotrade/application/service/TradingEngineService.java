package com.expotrade.application.service;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.model.Signal;
import com.expotrade.domain.model.StrategyConfig;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderType;
import com.expotrade.domain.model.enums.SignalType;
import com.expotrade.domain.model.enums.StrategyStatus;
import com.expotrade.domain.port.in.PlaceOrderUseCase;
import com.expotrade.domain.port.in.TradingStrategy;
import com.expotrade.domain.port.out.MarketDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradingEngineService {

    private static final Logger log = LoggerFactory.getLogger(TradingEngineService.class);

    private final List<TradingStrategy> tradingStrategies;
    private final StrategyService strategyService;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final MarketDataCache marketDataCache;

    public TradingEngineService(List<TradingStrategy> tradingStrategies,
                                StrategyService strategyService,
                                PlaceOrderUseCase placeOrderUseCase,
                                MarketDataCache marketDataCache) {
        this.tradingStrategies = tradingStrategies;
        this.strategyService = strategyService;
        this.placeOrderUseCase = placeOrderUseCase;
        this.marketDataCache = marketDataCache;
    }

    @Scheduled(fixedDelayString = "${trading.engine.interval:5000}")
    public void executeStrategies() {
        log.debug("Trading engine tick - evaluating active strategies");
    }

    public void evaluateStrategy(StrategyConfig config) {
        if (config.status() != StrategyStatus.ACTIVE) return;

        TradingStrategy strategy = tradingStrategies.stream()
                .filter(s -> s.supports(config.type()))
                .findFirst()
                .orElse(null);

        if (strategy == null) {
            log.warn("No strategy implementation found for type: {}", config.type());
            return;
        }

        for (String symbol : config.symbols()) {
            try {
                List<MarketData> history = marketDataCache.getHistory(symbol, 200);
                if (history.isEmpty()) continue;

                Signal signal = strategy.generateSignal(symbol, history, config);
                if (signal != null && signal.type() != SignalType.HOLD) {
                    processSignal(signal, config);
                }
            } catch (Exception e) {
                log.error("Error evaluating strategy {} for {}: {}", config.id(), symbol, e.getMessage());
            }
        }
    }

    private void processSignal(Signal signal, StrategyConfig config) {
        log.info("Signal: {} {} {} (strength: {})",
                signal.type(), signal.symbol(), signal.strategyId(), signal.strength());

        OrderSide side = signal.type() == SignalType.BUY ? OrderSide.BUY : OrderSide.SELL;

        var command = new PlaceOrderUseCase.PlaceOrderCommand(
                signal.symbol(), side, OrderType.MARKET,
                signal.suggestedQuantity(), signal.suggestedPrice(),
                signal.stopLoss(), signal.takeProfit(),
                config.brokerType(), config.id(), config.userId()
        );

        placeOrderUseCase.placeOrder(command)
                .subscribe(
                        order -> log.info("Order placed from signal: {}", order.id()),
                        error -> log.error("Failed to place order: {}", error.getMessage())
                );
    }
}
