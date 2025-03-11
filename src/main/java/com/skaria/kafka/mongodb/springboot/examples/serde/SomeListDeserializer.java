package com.skaria.kafka.mongodb.springboot.examples.serde;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaria.json.SystemObjectMapper;
import com.skaria.json.model.external.inbound.SomeListData;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.support.serializer.DeserializationException;

public class SomeListDeserializer implements Deserializer<SomeListData> {

    @Override
    public SomeListData deserialize(String s, byte[] bytes) {
        try {
            ObjectMapper objectMapper = SystemObjectMapper.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            return objectMapper.readValue(bytes, SomeListData.class);
        } catch (Exception e) {
            throw new DeserializationException(e.getMessage(), bytes, false, e.getCause());
        }
    }
}
