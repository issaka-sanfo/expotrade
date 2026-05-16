package com.expotrade.config;

import com.expotrade.adapters.web.websocket.MarketDataWebSocketHandler;
import com.expotrade.adapters.web.websocket.TradingWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MarketDataWebSocketHandler marketDataHandler;
    private final TradingWebSocketHandler tradingHandler;
    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(MarketDataWebSocketHandler marketDataHandler,
                           TradingWebSocketHandler tradingHandler,
                           WebSocketAuthInterceptor authInterceptor) {
        this.marketDataHandler = marketDataHandler;
        this.tradingHandler = tradingHandler;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(marketDataHandler, "/ws/market-data")
                .setAllowedOrigins("http://localhost:4200", "http://localhost:8100");

        registry.addHandler(tradingHandler, "/ws/events")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("http://localhost:4200", "http://localhost:8100");
    }
}
