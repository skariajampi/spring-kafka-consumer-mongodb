package com.skaria.kafka.mongodb.springboot.examples.service.ingest.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.result.UpdateResult;
import com.skaria.json.SystemObjectMapper;
import com.skaria.json.model.external.inbound.SomeListData;
import com.skaria.kafka.mongodb.springboot.examples.service.ingest.SomeListDataIngestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SomeListDataIngestServiceImpl implements SomeListDataIngestService {

    private final MongoTemplate mongoTemplate;
    private static final String COLLECTION_NAME = "someListData";
    public SomeListDataIngestServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    private void ingest(String data, String documentId) throws JsonProcessingException {
        //mongoTemplate.insert(data);
        Query query = new Query(Criteria.where("_id").is(documentId));
        Map<String, Object> fields = SystemObjectMapper.getInstance().readValue(data, Map.class);
        Update update = new Update();
        fields.forEach((k, v) -> update.set(k, v));
        UpdateResult updateResult = mongoTemplate.upsert(query, update, COLLECTION_NAME);
        log.info(" Document updated with document id: {} and modified count of {} ", documentId, updateResult.getModifiedCount());
    }

    @Override
    public void ingest(SomeListData data) throws JsonProcessingException {
        ingest(data.toJson(), data.getMessageId()+ "-" + data.getSystemId() + "-" + data.getName());
    }

}
