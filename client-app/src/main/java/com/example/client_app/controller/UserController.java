package com.example.client_app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.client_app.model.SellerModel;
import com.example.client_app.model.UserModel;


@Controller
@RequestMapping("/api/v1/register")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final static String USER_URL = "http://localhost:8090/api/v1/register";
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping
	public String showRegistrationForm(Model model) {
		UserModel user = new UserModel();
		SellerModel seller = new SellerModel();
		user.setSellermodel(seller);
		model.addAttribute("user", user);
		return "Register";
	}

	@PostMapping
	public String processRegistration(@ModelAttribute("user") UserModel userModel, Model model) {
		logger.info("Processing registration for user: {}", userModel.getUserEmail()); 

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(USER_URL,
					userModel, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				logger.info("User registered successfully: {}", userModel.getUserEmail());
				return "redirect:/api/v1/login";
				
			} else {
				logger.warn("Registration failed: {}", response.getBody());
				model.addAttribute("error", response.getBody());
				return "Register";
			}
			
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				logger.error("Registration failed with bad request: {}", e.getResponseBodyAsString());
				model.addAttribute("error", e.getResponseBodyAsString());
			} else {
				logger.error("Unexpected error occurred: {}", e.getMessage());
				model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
			}
			return "Register";
			
		} catch (Exception e) {
			logger.error("An error occurred during registration: {}", e.getMessage());
			model.addAttribute("error", "An unexpected error occurred");
			return "Register";
		}
	}

}
