package com.example.client_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.client_app.model.Cart;
import com.example.client_app.model.Product;
import com.example.client_app.model.UserModel;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/v1/cart")
public class CartController {

	private final static String USER_URL = "http://localhost:8090/api/v1/user?userId=";
	private final static String CART_URL = "http://localhost:8093/api/v1/cart";
	private final static String PRODUCT_URL = "http://localhost:8087/api/v1/products/";
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping
	public Mono<String> showCartPage(ServerWebExchange exchange, Model model) {
		WebClient webClient = webClientBuilder.build();

		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId")).map(cookie -> cookie.getValue())
				.flatMap(userId -> {
					return webClient.get().uri(USER_URL + userId).retrieve().bodyToMono(UserModel.class)
							.flatMap(userModel -> {
								if (userModel != null) {
									return webClient.post().uri(CART_URL + "/get").bodyValue(userModel).retrieve()
											.bodyToMono(List.class).flatMap(cartItems -> {
												model.addAttribute("cartItems", cartItems);
												return Mono.just("showCart");
											});
								} else {
									model.addAttribute("error", "User not found");
									return Mono.just("errorPage");
								}
							});
				}).switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}

	@PostMapping("/{productId}")
	public Mono<String> addToCart(ServerWebExchange exchange, Model model, @ModelAttribute Cart cart,
			@PathVariable("productId") int productId, @RequestParam("quantity") int quantity) {
		WebClient webClient = webClientBuilder.build();

		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId")).map(cookie -> cookie.getValue())
				.flatMap(userId -> {
					Mono<UserModel> userMono = webClient.get().uri(USER_URL + userId).retrieve()
							.bodyToMono(UserModel.class);

					Mono<Product> productMono = webClient.get()
							.uri(PRODUCT_URL + productId).retrieve()
							.bodyToMono(Product.class);

					return Mono.zip(userMono, productMono).flatMap(tuple -> {
						UserModel userModel = tuple.getT1();
						Product product = tuple.getT2();

						cart.setUser(userModel);
						cart.setQuantity(quantity);
						cart.setProduct(product);

						return webClient.post().uri(CART_URL + "/add").bodyValue(cart).retrieve()
								.bodyToMono(Integer.class).flatMap(status -> {
									if (status == 200) {
										return Mono.just("redirect:/api/v1/cart");
									} else {
										return Mono.just("notfound");
									}
								});
					});
				}).switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}


	@PostMapping("/delete")
	public Mono<String> deleteProduct(ServerWebExchange exchange, @RequestParam("productId") Integer productId) {

		WebClient webClient = webClientBuilder.build();
		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(userId -> {
	                return webClient.get()
	                        .uri(CART_URL + "/delete?productId=" + productId + "&userId=" + userId)
	                        .retrieve()
	                        .bodyToMono(Integer.class)
	                        .flatMap(status -> {
	                            if (status == 200) {
	                                return Mono.just("redirect:/api/v1/cart");
	                            } else {
	                                return Mono.just("notfound");
	                            }
	                        })
	                        .onErrorResume(e -> {
	                            return Mono.just("error");
	                        });
	            })
	            .switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}
	
	@PostMapping("/update")
	public Mono<String> updateQuantity(ServerWebExchange exchange, @RequestParam("productId") Integer productId,
			@RequestParam("quantity") Integer quantity) {

		WebClient webClient = webClientBuilder.build();
		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(userId -> {
	                return webClient.get()
	                        .uri(CART_URL + "/update?productId=" + productId + "&userId=" + userId + "&quantity=" + quantity)
	                        .retrieve()
	                        .bodyToMono(Integer.class)
	                        .flatMap(status -> {
	                            if (status == 200) {
	                                return Mono.just("redirect:/api/v1/cart");
	                            } else {
	                                return Mono.just("notfound");
	                            }
	                        })
	                        .onErrorResume(e -> {
	                            return Mono.just("error");
	                        });
	            })
	            .switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}
}
