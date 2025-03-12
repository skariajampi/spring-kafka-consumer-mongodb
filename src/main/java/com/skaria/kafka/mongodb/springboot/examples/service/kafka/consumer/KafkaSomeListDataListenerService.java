package com.skaria.kafka.mongodb.springboot.examples.service.kafka.consumer;

import com.skaria.json.model.external.inbound.SomeListData;
import com.skaria.kafka.mongodb.springboot.examples.service.ingest.impl.SomeListDataIngestServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaSomeListDataListenerService {

    private final SomeListDataIngestServiceImpl someListDataIngestService;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.source-topic}",
            autoStartup = "true",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "${spring.kafka.listener.concurrency}"
    )
    public void consumeSomeListData(ConsumerRecord<String, SomeListData> record,
                                    Acknowledgment ack,
                                    @Header(value = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer attempt) {
        try {
            someListDataIngestService.ingest(record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("delivery attempt # {} due to error {} ", attempt, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
