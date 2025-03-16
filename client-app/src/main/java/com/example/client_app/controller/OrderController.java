package com.example.client_app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;

import com.example.client_app.dto.OrderResponse;
import com.example.client_app.model.Cart;
import com.example.client_app.model.Order;
import com.example.client_app.model.OrderItem;
import com.example.client_app.model.UserModel;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final static String USER_URL = "http://localhost:8090/api/v1/user?userId=";
	private final static String CART_URL = "http://localhost:8093/api/v1/cart";
	private final static String ORDER_URL = "http://localhost:8099/api/v1/orders";

	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping
	public Mono<String> checkOut(Model model, ServerWebExchange exchange) {
		
		WebClient webClient = webClientBuilder.build();
		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(userId -> {
	                
	                return webClient.get()
	                        .uri(USER_URL + userId)
	                        .retrieve()
	                        .bodyToMono(UserModel.class)
	                        .flatMap(userModel -> {
	                            
	                            HttpHeaders headers = new HttpHeaders();
	                            HttpEntity<UserModel> requestEntity = new HttpEntity<>(userModel, headers);

	                            return webClient.post()
	                                    .uri(CART_URL + "/get")
	                                    .bodyValue(userModel) 
	                                    .retrieve()
	                                    .bodyToMono(new ParameterizedTypeReference<List<Cart>>() {
	                                    })
	                                    .flatMap(cartItems -> {
	                                     
	                                        Map<Integer, Integer> productQuantityMap = new HashMap<>();
	                                        double total = 0;

	                                        for (Cart item : cartItems) {
	                                            int productId = item.getProduct().getProductId();
	                                            int quantity = item.getQuantity();
	                                            double price = item.getProduct().getDiscountPrice();

	                                            productQuantityMap.put(productId,
	                                                    productQuantityMap.getOrDefault(productId, 0) + quantity);
	                                            total += quantity * price;
	                                        }

	                                        model.addAttribute("wallet", userModel.getWalletBalance());
	                                        model.addAttribute("total", total);
	                                        model.addAttribute("cartItems", cartItems);

	                                        return Mono.just("checkOut");
	                                    })
	                                    .onErrorResume(e -> {
	                                        model.addAttribute("error", "Unable to retrieve cart items.");
	                                        return Mono.just("errorPage");
	                                    });
	                        })
	                        .onErrorResume(e -> {
	                            model.addAttribute("error", "Unable to retrieve user details.");
	                            return Mono.just("errorPage");
	                        });
	            })
	            .switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}

	@GetMapping("/place")
    public Mono<?> placeOrder(ServerWebExchange exchange, @RequestParam("shippingAddress") String shippingAddress,
                                   @RequestParam("billingAddress") String billingAddress, Model model) {

        WebClient webClient = webClientBuilder.build();

        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
                .map(cookie -> cookie.getValue())
                .flatMap(userId -> webClient.get()
                        .uri(USER_URL + userId)
                        .retrieve()
                        .bodyToMono(UserModel.class)
                        .flatMap(user -> webClient.post()
                                .uri(CART_URL + "/get")
                                .bodyValue(user)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<List<Cart>>() {})
                                .flatMap(cartItems -> {
                                    if (cartItems.isEmpty()) {
                                        model.addAttribute("error", "Your cart is Empty");
                                        return Mono.just("checkOut");
                                    }

                                    OrderResponse orderRes = new OrderResponse();
                                    orderRes.setCart(cartItems);
                                    orderRes.setUser(user);
                                    orderRes.setBillingAddress(billingAddress);
                                    orderRes.setShippingAddress(shippingAddress);

                                    return webClient.post()
                                            .uri(ORDER_URL + "/place")
                                            .bodyValue(orderRes)
                                            .retrieve()
                                            .bodyToMono(Order.class)
                                            .flatMap(order -> webClient.post()
                                                    .uri(ORDER_URL + "/items")
                                                    .bodyValue(order)
                                                    .retrieve()
                                                    .bodyToMono(new ParameterizedTypeReference<List<OrderItem>>() {})
                                                    .flatMap(orderItems -> {
                                                        if (!orderItems.isEmpty()) {
                                                            orderItems.forEach(item -> template.convertAndSend(
                                                                    "/topic/notifications/" + item.getProduct().getSellerModel().getSellerId(),
                                                                    "New order placed: " + item.getProduct().getName()));
                                                        }
                                                        model.addAttribute("order", order);
                                                        model.addAttribute("orderItems", orderItems);
                                                        model.addAttribute("shippingAddress", shippingAddress);
                                                        model.addAttribute("billingAddress", billingAddress);
                                                        return Mono.just("orderConfirmation");
                                                    }))
                                            .onErrorResume(WebClientResponseException.class, e -> {
                                                model.addAttribute("error", "An error occurred: " + e.getMessage());
                                                return Mono.just("error");
                                            })
                                            .onErrorResume(e -> {
                                                model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
                                                return Mono.just("error");
                                            });
                                })
                        )
                );
    }

	@GetMapping("/orderHistory")
	public String orderHistoryBySeller(Model model) {
		List<Order> orders = restTemplate.getForObject(ORDER_URL + "/orderHistory", List.class);
		model.addAttribute("orders", orders);
		return "orderHistoryForSeller";
	}
	
	@GetMapping("/history")
	public Mono<String> orderHistory(Model model, ServerWebExchange exchange) {
	    WebClient webClient = webClientBuilder.build();

	    return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(userId -> {
	              
	                return webClient.get()
	                        .uri(ORDER_URL + "/history?userid=" + userId)
	                        .retrieve()
	                        .bodyToMono(new ParameterizedTypeReference<List<Order>>() {})
	                        .flatMap(orders -> {
	                            
	                            model.addAttribute("orders", orders);
	                            return Mono.just("orderHistory");
	                        })
	                        .onErrorResume(e -> {
	                            model.addAttribute("error", "Unable to retrieve order history.");
	                            return Mono.just("errorPage");
	                        });
	            })
	            .switchIfEmpty(Mono.just("redirect:/api/v1/login")); // If no userId cookie found, redirect to login
	}
	
	@GetMapping("/details")
	public String orderDetails(@RequestParam("orderId") int orderId, Model model) {
		List<OrderItem> orderItems = restTemplate.getForObject(ORDER_URL + "/details?orderId=" + orderId, List.class);
		model.addAttribute("orderItems", orderItems);
		return "orderDetails";
	}    	
}