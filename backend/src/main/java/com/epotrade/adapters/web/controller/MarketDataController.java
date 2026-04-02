package com.epotrade.adapters.web.controller;

import com.epotrade.application.service.MarketDataService;
import com.epotrade.domain.model.MarketData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market-data")
public class MarketDataController {
    private final MarketDataService marketDataService;
    public MarketDataController(MarketDataService marketDataService) { this.marketDataService = marketDataService; }

    @GetMapping("/{symbol}")
    public ResponseEntity<MarketData> getLatest(@PathVariable String symbol) {
        MarketData data = marketDataService.getLatestMarketData(symbol);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/stream/{broker}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MarketData> stream(@PathVariable String broker, @RequestParam List<String> symbols) {
        return marketDataService.getMarketDataStream(broker, symbols);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestParam String broker, @RequestParam List<String> symbols) {
        marketDataService.subscribeToMarketData(broker, symbols);
        return ResponseEntity.ok().build();
    }
}
