package com.github.arteam.analyze_it;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Component
public class RawPaymentsConsumer {

    private final Queue<UserPayment> userPayments = new ConcurrentLinkedQueue<UserPayment>();

    private final ObjectMapper objectMapper;

    public RawPaymentsConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "RAW_PAYMENTS", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    public void listen(String message) {
        /*try {
            messages.add(objectMapper.readValue(message, WordCount.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
        try {
            userPayments.add(objectMapper.readValue(message, UserPayment.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<UserPayment> getUserPayments() {
        return List.copyOf(userPayments);
    }
}
