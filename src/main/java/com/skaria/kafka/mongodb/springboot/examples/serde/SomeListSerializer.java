package com.skaria.kafka.mongodb.springboot.examples.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skaria.json.SystemObjectMapper;
import com.skaria.json.model.external.inbound.SomeListData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class SomeListSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(String s, Object o) {
        try {
            if (o == null) {
                return null;
            }
            if(o instanceof byte[]) {
                return (byte[])o;
            }
            if(o instanceof SomeListData) {
                SomeListData someListData = (SomeListData)o;
                ObjectMapper objectMapper = SystemObjectMapper.getInstance().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                return objectMapper.writeValueAsString(someListData).getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SerializationException("Exception while serializing SomeListData object", e);
        }
        return o.toString().getBytes(StandardCharsets.UTF_8);
    }
}
