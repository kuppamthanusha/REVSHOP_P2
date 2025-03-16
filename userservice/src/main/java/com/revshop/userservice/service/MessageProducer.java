//package com.revshop.userservice.service;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//import com.revshop.userservice.dto.LoginResponse;
//
//@Service
//public class MessageProducer {
//
//	private KafkaTemplate<String, LoginResponse> kafkaTemplate;
//	
//	public MessageProducer(KafkaTemplate<String, LoginResponse> kafkaTemplate) {
//		this.kafkaTemplate = kafkaTemplate;
//	}
//	
//	
//	public void sendMessage(LoginResponse data) {
//		Message<LoginResponse> message = MessageBuilder
//				.withPayload(data)
//				.setHeader(KafkaHeaders.TOPIC, "kafka")
//				.build();
//				 
//        kafkaTemplate.send(message);
//    }
//}
