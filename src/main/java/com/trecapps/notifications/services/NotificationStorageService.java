package com.trecapps.notifications.services;

import com.azure.core.credential.AzureNamedKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.trecapps.notifications.models.NotificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class NotificationStorageService {
    BlobServiceClient client;

    ObjectMapper objectMapper;

    Logger logger = LoggerFactory.getLogger(NotificationStorageService.class);

    @Autowired
    NotificationStorageService(@Value("${trec-not.storage.account-name}") String name,
                       @Value("${trec-not.storage.account-key}") String key,
                       @Value("${trec-not.storage.blob-endpoint}") String endpoint,
                       Jackson2ObjectMapperBuilder objectMapperBuilder)
    {
        AzureNamedKeyCredential credential = new AzureNamedKeyCredential(name, key);
        client = new BlobServiceClientBuilder().credential(credential).endpoint(endpoint).buildClient();
        objectMapper = objectMapperBuilder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void saveNotifications(NotificationList notifications, String userId)
    {
        BlobContainerClient containerClient = client.getBlobContainerClient("trec-apps-users");

        BlobClient client = containerClient.getBlobClient("notify-" + userId);

        client.upload(BinaryData.fromObject(notifications),true);
    }

    public NotificationList retrieveNotifications(String userId) throws JsonProcessingException
    {
        BlobContainerClient containerClient = client.getBlobContainerClient("trec-apps-users");

        BlobClient client = containerClient.getBlobClient("notify-" + userId);

        BinaryData bData = client.downloadContent();

        String data = new String(bData.toBytes(), StandardCharsets.UTF_8);

        return objectMapper.readValue(data, NotificationList.class);
    }
}
