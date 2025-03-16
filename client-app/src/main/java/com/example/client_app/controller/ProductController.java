package com.example.client_app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.example.client_app.model.Category;
import com.example.client_app.model.Product;
import com.example.client_app.model.Review;

@Controller
@RequestMapping
public class ProductController {

	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/api/v1/buyer-dashboard")
	public String viewDashboard() {
		return "buyerDashboard";
	}

	@GetMapping("/api/v1/seller")
	public String showSeller() {
		return "sellerDashboard";
	}

	@GetMapping("/api/v1/products")
	public Mono<String> viewProductsPage(ServerWebExchange exchange, Model model) {
		WebClient webClient = webClientBuilder.build();

		return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("userId")).flatMap(cookie -> {
			String userId = cookie.getValue();

			Mono<List<Product>> productsMono = webClient.get().uri("http://localhost:8087/api/v1/products").retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<Product>>() {
					});

			Mono<List<Category>> categoriesMono = webClient.get().uri("http://localhost:8087/api/v1/categories")
					.retrieve().bodyToMono(new ParameterizedTypeReference<List<Category>>() {
					});

			Mono<List<Product>> wishlistMono = webClient.get()
					.uri("http://localhost:8088/api/v1/wishlist?userId=" + userId).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<Product>>() {
					});

			return Mono.zip(productsMono, categoriesMono, wishlistMono).map(tuple -> {
				List<Product> products = tuple.getT1();
				List<Category> categories = tuple.getT2();
				List<Product> wishlistProducts = tuple.getT3();

				model.addAttribute("products", products);
				model.addAttribute("categories", categories);
				model.addAttribute("wishlistProducts", wishlistProducts);
				return "view001";
			});
		}).switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	}

	@GetMapping("/api/v1/products/{id}")
	public Mono<String> viewProductsById(@PathVariable("id") int productId, Model model) {
		WebClient webClient = webClientBuilder.build();

		Mono<Product> products = webClient.get().uri("http://localhost:8087/api/v1/products/" + productId).retrieve()
				.bodyToMono(Product.class);
		List<Review> reviews = restTemplate.getForObject("http://localhost:8049/api/v1/reviews/get?productId=" + productId, List.class);
		model.addAttribute("product", products);
		model.addAttribute("reviews", reviews);
		return Mono.just("ProductDetails");
	}

	@GetMapping("/api/v1/seller/products")
    public Mono<String> showProducts(ServerWebExchange exchange, Model model) {
        WebClient webClient = webClientBuilder.build();

        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("sellerId"))
            .map(cookie -> cookie.getValue())
            .flatMap(sellerId -> {
                System.out.println("product: " + sellerId);

                return webClient.get()
                    .uri("http://localhost:8087/api/v1/seller/products?sellerid=" + sellerId)
                    .retrieve()
                    .bodyToMono(List.class)
                    .flatMap(products -> {
                        model.addAttribute("products", products);              
                        return Mono.just("sellerProducts");
                    })
                    .onErrorResume(e -> {
                        System.err.println("Error fetching products: " + e.getMessage());
                        model.addAttribute("error", "Unable to fetch products. Please try again later.");
                        return Mono.just("errorPage");
                    });
            })
            .switchIfEmpty(Mono.just("redirect:/api/v1/login"));
    }
}
