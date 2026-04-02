package com.epotrade.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean public NewTopic ordersTopic() { return TopicBuilder.name("epotrade.orders").partitions(3).replicas(1).build(); }
    @Bean public NewTopic tradesTopic() { return TopicBuilder.name("epotrade.trades").partitions(3).replicas(1).build(); }
    @Bean public NewTopic marketDataTopic() { return TopicBuilder.name("epotrade.market-data").partitions(6).replicas(1).build(); }
    @Bean public NewTopic strategiesTopic() { return TopicBuilder.name("epotrade.strategies").partitions(3).replicas(1).build(); }
}
