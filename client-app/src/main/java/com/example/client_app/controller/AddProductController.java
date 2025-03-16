package com.example.client_app.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.client_app.model.Category;
import com.example.client_app.model.Product;
import com.example.client_app.model.Size;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/v1")
public class AddProductController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping("/sizes")
	@ResponseBody
	public List<Size> getAllSizes(@RequestParam("categoryId") int categoryId) {
		List<Size> sizes = restTemplate.getForObject("http://localhost:8087/api/v1/sizes?categoryId= " + categoryId,
				List.class);
		return sizes;
	}

	@GetMapping("/addForm")
	public String showProductPage(Model model) {
		model.addAttribute("Product", new Product());
		List<Category> categories = restTemplate.getForObject("http://localhost:8087/api/v1/categories", List.class);
		model.addAttribute("categories", categories);
		return "addProductsToInventory";
	}
	
	@PostMapping("/add")
	public Mono<String> addProductToInventory(@RequestParam("size") int sizeId, @ModelAttribute Product product, ServerWebExchange exchange) {
	    WebClient webClient = webClientBuilder.build();
	
	    return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("sellerId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(sellerId -> {
	            	System.out.println("SellerId" + sellerId);
	            	
	                return webClient.post()
	                        .uri("http://localhost:8087/api/v1/add?sizeId=" + sizeId + "&sellerid=" + sellerId)
	                        .bodyValue(product)
	                        .retrieve()
	                        .bodyToMono(String.class)
	                        .flatMap(response -> {
	                            return Mono.just("redirect:/api/v1/seller/products");
	                         
	                        });
	            }).onErrorResume(e -> {
	            	 return Mono.just("errorPage");
	            });
	}


	@GetMapping("/update")
	public String showUpdateForm(@RequestParam("productId") int productId, Model model) {
		Product product = restTemplate.getForObject("http://localhost:8087/api/v1/update?productId=" + productId,
				Product.class);
		List<Category> categories = restTemplate.getForObject("http://localhost:8087/api/v1/categories", List.class);
		model.addAttribute("categories", categories);
		model.addAttribute("product", product);
		return "updateProductToInventory";
	}
	
	@PostMapping("/update")
	public Mono<String> updateProductToInventory(@ModelAttribute Product product, ServerWebExchange exchange, 
			@RequestParam("productId") int productId) {
	    WebClient webClient = webClientBuilder.build();

	    return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("sellerId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(sellerId -> {
	            	System.out.println("SellerId" + sellerId);
	              
	                return webClient.post()
	                        .uri("http://localhost:8087/api/v1/update?productId=" + productId+ "&sellerid=" + sellerId)
	                        .bodyValue(product)
	                        .retrieve()
	                        .bodyToMono(String.class)
	                        .flatMap(response -> {
	                            
	                            return Mono.just("redirect:/api/v1/seller/products");
	                         
	                        });
	            }).onErrorResume(e -> {
	            	 return Mono.just("errorPage");
	            });
	}

	@PostMapping("/delete")
	public Mono<String> deleteProuctFromInventory(ServerWebExchange exchange, @RequestParam("productId") int productId) {
	    WebClient webClient = webClientBuilder.build();
	    System.out.println("Hello Delete");
	    return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst("sellerId"))
	            .map(cookie -> cookie.getValue())
	            .flatMap(sellerId -> {	
	            	System.out.println("SellerId" + sellerId);
	                return webClient.post()
	                        .uri("http://localhost:8087/api/v1/delete?productId=" + productId + "&sellerid=" + sellerId)
	                        .retrieve()
	                        .bodyToMono(String.class)
	                        .flatMap(response -> {
	                        	System.out.println("response" + response);
	                            return Mono.just("redirect:/api/v1/seller/products");
	                        });
	            })
	            .switchIfEmpty(Mono.just("redirect:/api/v1/login"));
	            		
	}
}