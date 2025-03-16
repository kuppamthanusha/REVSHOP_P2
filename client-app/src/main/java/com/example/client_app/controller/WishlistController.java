package com.example.client_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping
	public Mono<String> showWishlist(ServerWebExchange exchange, Model model) {
		WebClient webClient = webClientBuilder.build();

		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId")).map(cookie -> cookie.getValue())
				.flatMap(userId -> {
					System.out.println("userId: " + userId);

					return webClient.get().uri("http://localhost:8088/api/v1/wishlist?userId=" + userId).retrieve()
							.bodyToMono(List.class).flatMap(products -> {
								model.addAttribute("products", products);
								return Mono.just("wishlist");
							}).onErrorResume(e -> {
								System.err.println("Error fetching wishlist: " + e.getMessage());
								model.addAttribute("error", "Unable to fetch wishlist. Please try again later.");
								return Mono.just("errorPage");
							}).switchIfEmpty(Mono.just("redirect:/api/v1/login"));
				});
	}

	@PostMapping("/{productId}")
	public Mono<String> addToWishlist(ServerWebExchange exchange, Model model, @PathVariable("productId") int productId) {
	    WebClient webClient = webClientBuilder.build();

	    return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
	        .map(cookie -> cookie.getValue())
	        .flatMap(userId -> {
	            System.out.println("userId: " + userId + ", productId: " + productId);

	            return webClient.get()
	                .uri("http://localhost:8088/api/v1/wishlist/add?userId=" + userId + "&productId=" + productId)
	                .retrieve()
	                .bodyToMono(Integer.class) 
	                .flatMap(status -> {
	                    if (status == 200) {
	                        return Mono.just("redirect:/api/v1/products");
	                    } else {
	                        model.addAttribute("error", "Unable to add product to wishlist. Please try again later.");
	                        return Mono.just("redirect:/api/v1/wishlist");
	                    }
	                })
	                .onErrorResume(e -> {
	                    model.addAttribute("error", "An error occurred while adding to the wishlist.");
	                    return Mono.just("errorPage");
	                });
	        })
	        .switchIfEmpty(Mono.just("redirect:/api/v1/login")); // Redirect to login if no userId cookie is found
	}


	@DeleteMapping
	public Mono<String> removeProduct(ServerWebExchange exchange, Model model, @RequestParam("productId") Integer productId) {
		WebClient webClient = webClientBuilder.build();

		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId")).map(cookie -> cookie.getValue())
				.flatMap(userId -> {
					System.out.println("userId: " + userId);

					return webClient.get().uri(
							"http://localhost:8088/api/v1/wishlist/delete?userId=" + userId + "&productId=" + productId)
							.retrieve().bodyToMono(Integer.class).flatMap(status -> {
								if (status == 200) {
									return Mono.just("redirect:/api/v1/products");
								} else {
									return Mono.just("notfound");
								}
							}).onErrorResume(e -> {
								System.err.println("Error removing product from wishlist: " + e.getMessage());
								model.addAttribute("error",
										"Unable to remove product from wishlist. Please try again later.");
								return Mono.just("errorPage");
							});
				}).switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}
}