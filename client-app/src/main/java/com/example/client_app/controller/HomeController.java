package com.example.client_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

	@GetMapping
	public String showHome() {
		return "index";
	}
	
	@GetMapping("/logout")
	public String logout() {
		return "redirect:/api/v1/login";
	}
}
