package com.skaria.kafka.mongodb.springboot.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SampleMongoDbIngestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleMongoDbIngestApplication.class, args);
    }
}
