package com.expotrade.infrastructure.cache;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.port.out.MarketDataCache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class RedisMarketDataCache implements MarketDataCache {

    private static final Logger log = LoggerFactory.getLogger(RedisMarketDataCache.class);
    private static final String KEY_PREFIX = "market:";
    private static final String HISTORY_PREFIX = "market:history:";
    private static final int MAX_HISTORY = 500;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisMarketDataCache(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void store(MarketData data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(KEY_PREFIX + data.symbol(), json);
            redisTemplate.opsForList().leftPush(HISTORY_PREFIX + data.symbol(), json);
            redisTemplate.opsForList().trim(HISTORY_PREFIX + data.symbol(), 0, MAX_HISTORY - 1);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize market data for {}", data.symbol(), e);
        }
    }

    @Override
    public Optional<MarketData> getLatest(String symbol) {
        try {
            String json = redisTemplate.opsForValue().get(KEY_PREFIX + symbol);
            if (json != null) return Optional.of(objectMapper.readValue(json, MarketData.class));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize market data for {}", symbol, e);
        }
        return Optional.empty();
    }

    @Override
    public List<MarketData> getHistory(String symbol, int limit) {
        try {
            List<String> jsonList = redisTemplate.opsForList().range(HISTORY_PREFIX + symbol, 0, limit - 1);
            if (jsonList != null) {
                return jsonList.stream().map(json -> {
                    try { return objectMapper.readValue(json, MarketData.class); }
                    catch (JsonProcessingException e) { return null; }
                }).filter(Objects::nonNull).toList();
            }
        } catch (Exception e) {
            log.error("Failed to get history for {}", symbol, e);
        }
        return Collections.emptyList();
    }
}
