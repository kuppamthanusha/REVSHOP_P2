package com.example.client_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.client_app.model.LoginModel;
import com.example.client_app.model.LoginResponse;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/v1/login")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private final static String USER_URL = "http://localhost:8090/api/v1/login";
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping
	public String showLoginForm(Model model) {
		model.addAttribute("login", new LoginModel());
		return "Login";
	}

	@PostMapping
	public Mono<String> processLogin(@ModelAttribute("login") LoginModel loginModel, Model model,
			ServerWebExchange exchange) {
			return webClientBuilder.build().post().uri(USER_URL).bodyValue(loginModel)
					.retrieve().bodyToMono(LoginResponse.class).flatMap(res -> {
						if (res != null) {
							Integer fetchedUserId = res.getId();
							logger.info("Login successful for userId: {}", fetchedUserId);
							if ("buyer".equals(res.getRole())) {
								ResponseCookie userIdCookie = ResponseCookie.from("userId", fetchedUserId.toString())
										.httpOnly(true).path("/").maxAge(24 * 60 * 60).build();
	
								exchange.getResponse().addCookie(userIdCookie);
								return Mono.just("redirect:/api/v1/buyer-dashboard");
							} else if ("seller".equals(res.getRole())) {
								ResponseCookie userIdCookie = ResponseCookie.from("sellerId", fetchedUserId.toString())
										.httpOnly(true).path("/").maxAge(24 * 60 * 60).build();
	
								exchange.getResponse().addCookie(userIdCookie);
	
								return Mono.just("redirect:/api/v1/seller");
							} else {
								model.addAttribute("error", "Unknown user role.");
								logger.warn("Unknown user role for userId: {}", fetchedUserId);
								return Mono.just("Login");
							}
						} else {
							model.addAttribute("error", "Invalid email or password.");
							logger.warn("Login failed for email: {}. Response was null.", loginModel.getUserEmail());
							return Mono.just("Login");
						}
				}).onErrorResume(e -> {
					model.addAttribute("error", "An error occurred during login. Please try again.");
					logger.error("An unexpected error occurred during login for email: {}. Error: {}", loginModel.getUserEmail(), e.getMessage());
					return Mono.just("Login");
				});
	}
}
