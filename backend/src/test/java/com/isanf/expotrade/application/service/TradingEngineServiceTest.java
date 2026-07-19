package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.Signal;
import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import com.isanf.expotrade.domain.model.enums.SignalType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import com.isanf.expotrade.domain.port.in.PlaceOrderUseCase;
import com.isanf.expotrade.domain.port.in.TradingStrategy;
import com.isanf.expotrade.domain.port.out.MarketDataCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradingEngineServiceTest {

    private TradingStrategy tradingStrategy;
    private StrategyService strategyService;
    private PlaceOrderUseCase placeOrderUseCase;
    private MarketDataCache marketDataCache;
    private TradingEngineService service;

    @BeforeEach
    void setUp() {
        tradingStrategy = mock(TradingStrategy.class);
        strategyService = mock(StrategyService.class);
        placeOrderUseCase = mock(PlaceOrderUseCase.class);
        marketDataCache = mock(MarketDataCache.class);
        service = new TradingEngineService(List.of(tradingStrategy), strategyService, placeOrderUseCase, marketDataCache);
    }

    @Test
    void givenActiveStrategyAndBuySignalWhenEvaluatingThenPlacesMarketBuyOrder() {
        StrategyConfig config = config("strategy-1", StrategyStatus.ACTIVE);
        List<MarketData> history = List.of(marketData("AAPL"));
        when(tradingStrategy.supports("RSI")).thenReturn(true);
        when(marketDataCache.getHistory("AAPL", 200)).thenReturn(history);
        when(tradingStrategy.generateSignal("AAPL", history, config)).thenReturn(signal(SignalType.BUY));
        when(placeOrderUseCase.placeOrder(any(PlaceOrderUseCase.PlaceOrderCommand.class)))
                .thenReturn(Mono.just(order(config)));

        service.evaluateStrategy(config);

        verify(placeOrderUseCase).placeOrder(argThat(command ->
                command.symbol().equals("AAPL")
                        && command.side() == OrderSide.BUY
                        && command.type() == OrderType.MARKET
                        && command.brokerType() == BrokerType.IBKR
                        && command.strategyId().equals(config.id())
                        && command.userId().equals(config.userId())));
    }

    @Test
    void givenHoldSignalWhenEvaluatingThenDoesNotPlaceOrder() {
        StrategyConfig config = config("strategy-1", StrategyStatus.ACTIVE);
        List<MarketData> history = List.of(marketData("AAPL"));
        when(tradingStrategy.supports("RSI")).thenReturn(true);
        when(marketDataCache.getHistory("AAPL", 200)).thenReturn(history);
        when(tradingStrategy.generateSignal("AAPL", history, config)).thenReturn(signal(SignalType.HOLD));

        service.evaluateStrategy(config);

        verify(placeOrderUseCase, never()).placeOrder(any());
    }

    @Test
    void givenPausedStrategyWhenEvaluatingThenDoesNotReadHistoryOrPlaceOrder() {
        StrategyConfig config = config("strategy-1", StrategyStatus.PAUSED);

        service.evaluateStrategy(config);

        verifyNoInteractions(marketDataCache, placeOrderUseCase);
    }

    @Test
    void givenMultipleActiveStrategiesWhenOneFailsThenEngineContinues() {
        StrategyConfig failing = config("strategy-1", StrategyStatus.ACTIVE);
        StrategyConfig succeeding = config("strategy-2", StrategyStatus.ACTIVE);
        when(strategyService.getActiveStrategies()).thenReturn(List.of(failing, succeeding));
        when(tradingStrategy.supports("RSI")).thenReturn(true);
        when(marketDataCache.getHistory("AAPL", 200))
                .thenThrow(new IllegalStateException("cache unavailable"))
                .thenReturn(List.of(marketData("AAPL")));
        when(tradingStrategy.generateSignal(eq("AAPL"), any(), eq(succeeding))).thenReturn(signal(SignalType.SELL));
        when(placeOrderUseCase.placeOrder(any(PlaceOrderUseCase.PlaceOrderCommand.class)))
                .thenReturn(Mono.just(order(succeeding)));

        service.executeStrategies();

        verify(marketDataCache, times(2)).getHistory("AAPL", 200);
        verify(placeOrderUseCase).placeOrder(argThat(command ->
                command.side() == OrderSide.SELL && command.strategyId().equals(succeeding.id())));
    }

    private StrategyConfig config(String id, StrategyStatus status) {
        return new StrategyConfig(id, "RSI Strategy", "RSI", List.of("AAPL"),
                BrokerType.IBKR, status, BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.TEN,
                Map.of("period", "14"), UUID.randomUUID());
    }

    private MarketData marketData(String symbol) {
        BigDecimal price = BigDecimal.valueOf(150);
        return new MarketData(symbol, price, price, price, BigDecimal.valueOf(1000),
                price, price, price, price, Instant.now());
    }

    private Signal signal(SignalType type) {
        return new Signal("strategy-1", "AAPL", type, BigDecimal.ONE,
                BigDecimal.valueOf(150), BigDecimal.TEN, null, null, "test", Instant.now());
    }

    private Order order(StrategyConfig config) {
        return Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                config.brokerType(), config.id(), config.userId()).withStatus(OrderStatus.SUBMITTED);
    }
}
