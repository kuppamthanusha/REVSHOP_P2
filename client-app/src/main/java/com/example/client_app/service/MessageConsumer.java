//package com.example.client_app.service;
//
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import com.example.client_app.model.LoginResponse;
//
//@Service
//public class MessageConsumer {
//
//	@KafkaListener(topics="kafka", groupId="group-1")
//	public void getMessage(LoginResponse res) {
//		System.out.println(res.getRole());
//	}
//}
