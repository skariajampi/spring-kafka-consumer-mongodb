package com.skaria.kafka.mongodb.springboot.examples.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@Slf4j
public class MongoConfig {

    private final Environment environment;
    private final String mongoConnectionUri;
    private final String mongoDBUsername;
    private final String mongoDBPassword;
    private final String mongoDBName;
    private final String keyStorePath;
    private final String keyStorePassword;

    public MongoConfig(Environment environment,
                       @Value("${spring.data.mongodb.uri}") String mongoConnectionUri,
                       @Value("${spring.data.mongodb.username}")String mongoDBUsername,
                       @Value("${spring.data.mongodb.password}")String mongoDBPassword,
                       @Value("${spring.data.mongodb.database}")String mongoDBName,
                       @Value("${mongodb-config.mongodb-ssl-keystore}")String keyStorePath,
                       @Value("${mongodb-config.mongodb-ssl-keystore-password}")String keyStorePassword) {
        this.environment = environment;
        this.mongoConnectionUri = mongoConnectionUri;
        this.mongoDBUsername = mongoDBUsername;
        this.mongoDBPassword = mongoDBPassword;
        this.mongoDBName = mongoDBName;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
    }

    @Bean
    public MongoClient mongoClient() throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException,
            KeyManagementException {
        String activeSpringProfile = getActiveSpringProfile();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        try(FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        MongoClientSettings.Builder mongoClientSettingsBuilder = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoConnectionUri))
                .applyToSslSettings(builder -> builder.enabled(true).context(sslContext));

        if(!(activeSpringProfile.equals("local") || activeSpringProfile.equals("test"))) {
            MongoCredential mongoCredential = MongoCredential.createScramSha256Credential(mongoDBUsername, "admin", mongoDBPassword.toCharArray());
            mongoClientSettingsBuilder.credential(mongoCredential);
        }
        MongoClientSettings mongoClientSettings = mongoClientSettingsBuilder.build();
        return MongoClients.create(mongoClientSettings);
    }

    private String getActiveSpringProfile() {
        Optional<String> activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst();
        return activeProfile.orElseThrow(() -> new RuntimeException("No active profile found"));
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() throws UnrecoverableKeyException, CertificateException,
            KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient(), mongoDBName));
    }
}
