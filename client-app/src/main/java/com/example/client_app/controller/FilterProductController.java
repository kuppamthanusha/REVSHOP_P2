package com.example.client_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.client_app.model.Category;
import com.example.client_app.model.Gender;
import com.example.client_app.model.Product;
import com.example.client_app.model.Size;

@Controller
@RequestMapping("/api/v1")
public class FilterProductController {

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
	private WebClient.Builder webClientBuilder;

    @GetMapping("/allfetchedproducts")
    public String showFilteredProducts(
            @RequestParam(required = false) List<String> gender,
            @RequestParam(required = false) List<String> price, // Still a list here
            @RequestParam(required = false) List<Integer> category,
            @RequestParam(required = false) List<Integer> size,
            ServerWebExchange exchange,
            Model model) {

    	WebClient webClient = webClientBuilder.build();
    	Integer userId = Integer.valueOf(exchange.getRequest().getCookies().getFirst("userId").getValue());
    	
        List<Category> categories = restTemplate.getForObject("http://localhost:8087/api/v1/categories", List.class);
        model.addAttribute("categories", categories);

        List<Integer> categoryId = category != null ? category : List.of();
        List<Integer> sizeId = size != null ? size : List.of();

        List<Gender> selectedGenders = (gender != null && !gender.isEmpty())
                ? gender.stream().map(Gender::valueOf).collect(Collectors.toList())
                : List.of();

        List<String> priceRanges = price != null ? price : List.of();

        StringBuilder uriBuilder = new StringBuilder("http://localhost:8087/api/v1/products/filter?");
        
        if (!selectedGenders.isEmpty()) {
            uriBuilder.append("gender=").append(selectedGenders.stream().map(Enum::name).collect(Collectors.joining(","))).append("&");
        }
        if (!categoryId.isEmpty()) {
            uriBuilder.append("category=").append(categoryId.stream().map(String::valueOf).collect(Collectors.joining(","))).append("&");
        }
        if (!sizeId.isEmpty()) {
            uriBuilder.append("size=").append(sizeId.stream().map(String::valueOf).collect(Collectors.joining(","))).append("&");
        }
        if (!priceRanges.isEmpty()) {
            uriBuilder.append("price=").append(String.join(",", priceRanges)).append("&");
        }

        List<Product> fetchedProducts = restTemplate.getForObject(uriBuilder.toString(), List.class);

        if (fetchedProducts == null || fetchedProducts.isEmpty()) {
            fetchedProducts = restTemplate.getForObject("http://localhost:8087/api/v1/products", List.class);
            model.addAttribute("errorMessage", "No products available ");
        }

        List<Product> wishlistProducts = restTemplate.getForObject("http://localhost:8088/api/v1/wishlist?userId=" + userId, List.class);

        model.addAttribute("wishlistProducts", wishlistProducts);
        model.addAttribute("products", fetchedProducts);
        return "view001";
    }

    @GetMapping("/dynamicsizes")
    @ResponseBody
    public List<Size> getSizesByCategory(@RequestParam("categoryId") List<Integer> categoryIds) {
        return restTemplate.getForObject("http://localhost:8087/api/v1/sizes?categoryId=" + categoryIds.stream().map(String::valueOf).collect(Collectors.joining(",")), List.class);
    }
}