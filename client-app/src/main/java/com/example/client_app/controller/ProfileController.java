package com.example.client_app.controller;

import com.example.client_app.model.*;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

@Controller
@RequestMapping("/api/v1/profile")
public class ProfileController {
	
	private final static String USER_URL = "http://localhost:8090/api/v1/user?userId=";
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping
	public Mono<String> viewProfile(Model model, ServerWebExchange exchange) {
		WebClient webClient = webClientBuilder.build();
		
		Integer userId = Integer.valueOf(exchange.getRequest().getCookies().getFirst("userId").getValue());
		if (userId == null) {
			return Mono.just("redirect:/api/v1/login");
		}
		
		Mono<UserModel> user = webClient.get().uri(USER_URL + userId).retrieve().bodyToMono(UserModel.class);
		model.addAttribute("user", user);
		return Mono.just("profile");
	}

//	@GetMapping("/edit")
//	public String editProfile(HttpSession session, Model model) {
//		Integer userId = (Integer) session.getAttribute("userId");
//
//		if (userId == null) {
//			return "redirect:/api/v1/login";
//		}
//
//		UserModel user = userService.getUserId(userId);
//		model.addAttribute("user", user);
//		return "editProfile";
//	}
//
//	@PostMapping("/edit")
//	public String updateProfile(@ModelAttribute("user") UserModel updatedUser, HttpSession session) {
//		Integer userId = (Integer) session.getAttribute("userId");
//
//		if (userId == null) {
//			return "redirect:/api/v1/login";
//		}
//
//		UserModel existingUser = userService.getUserId(userId);
//
//		if (existingUser != null) {
//			existingUser.setUserName(updatedUser.getUserName());
//			existingUser.setUserMobileNumber(updatedUser.getUserMobileNumber());
//			existingUser.setUserPassword(updatedUser.getUserPassword());
//			userService.updateUserProfile(existingUser);
//		}
//
//		return "redirect:/api/v1/profile";
//	}

}
