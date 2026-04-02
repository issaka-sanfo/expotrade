package com.expotrade.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean public NewTopic ordersTopic() { return TopicBuilder.name("expotrade.orders").partitions(3).replicas(1).build(); }
    @Bean public NewTopic tradesTopic() { return TopicBuilder.name("expotrade.trades").partitions(3).replicas(1).build(); }
    @Bean public NewTopic marketDataTopic() { return TopicBuilder.name("expotrade.market-data").partitions(6).replicas(1).build(); }
    @Bean public NewTopic strategiesTopic() { return TopicBuilder.name("expotrade.strategies").partitions(3).replicas(1).build(); }
}
