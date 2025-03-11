package com.skaria.kafka.mongodb.springboot.examples.config;

import com.skaria.json.model.external.inbound.SomeListData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private final ConsumerFactory<String, SomeListData> kafkaConsumerFactory;
    private final ProducerFactory<String, Object> kafkaProducerfactory;
    private final Integer maxRetries;
    private final Integer maxInterval;
    private final Integer initialBackoffInterval;
    private final Integer backoffMultiplier;

    public KafkaConsumerConfig(ConsumerFactory<String, SomeListData> kafkaConsumerFactory,
                               ProducerFactory<String, Object> kafkaProducerfactory,
                               @Value("${spring.kafka.consumer.properties.retries.max}") Integer maxRetries,
                               @Value("${spring.kafka.consumer.properties.retries.interval}")Integer maxInterval,
                               @Value("${spring.kafka.consumer.properties.retries.initialInterval}")Integer initialBackoffInterval,
                               @Value("${spring.kafka.consumer.properties.retries.backoffMultiplier}")Integer backoffMultiplier) {
        this.kafkaConsumerFactory = kafkaConsumerFactory;
        this.kafkaProducerfactory = kafkaProducerfactory;
        this.maxRetries = maxRetries;
        this.maxInterval = maxInterval;
        this.initialBackoffInterval = initialBackoffInterval;
        this.backoffMultiplier = backoffMultiplier;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SomeListData> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SomeListData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler(kafkaTemplateForErrorHandler()));
        factory.getContainerProperties().setDeliveryAttemptHeader(true);
        return factory;
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        ExponentialBackOffWithMaxRetries backOffWithMaxRetries = new ExponentialBackOffWithMaxRetries(maxRetries);
        backOffWithMaxRetries.setMaxInterval(maxInterval);
        backOffWithMaxRetries.setInitialInterval(initialBackoffInterval);
        backOffWithMaxRetries.setMultiplier(backoffMultiplier);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, backOffWithMaxRetries);
        errorHandler.setLogLevel(KafkaException.Level.ERROR);
        errorHandler.setRetryListeners((consumerRecord, e, i) -> {
            log.error("Retry attempt # {} of {} , with exception : {} ",
                    i, maxRetries, e.getMessage());
        });

        return errorHandler;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplateForErrorHandler() {
        return new KafkaTemplate<>(kafkaProducerfactory);
    }

}
