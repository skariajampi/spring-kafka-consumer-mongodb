package com.skaria.kafka.mongodb.springboot.examples.service.ingest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skaria.json.model.external.inbound.SomeListData;

public interface SomeListDataIngestService {

    void ingest(SomeListData data) throws JsonProcessingException;
}
