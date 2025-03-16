package com.revshop.productservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revshop.productservice.entity.Product;
import com.revshop.productservice.enums.Gender;
import com.revshop.productservice.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class FilterProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/filter")
	public ResponseEntity<List<Product>> filterProducts(
			@RequestParam(name = "gender", required = false) List<String> gender,
			@RequestParam(name = "category", required = false) List<Integer> category,
			@RequestParam(name = "size", required = false) List<Integer> size,
			@RequestParam(name = "price", required = false) List<String> price
	) {

		double minPrice = 0.0;
		double maxPrice = Double.MAX_VALUE;

		if (price != null) {
			if (price.contains("under2000")) {
				maxPrice = 2000.00;
			}
			if (price.contains("2000to3500")) {
				minPrice = Math.max(minPrice, 2000.00);
				maxPrice = Math.min(maxPrice, 3500.00);
			}
			if (price.contains("3500to6000")) {
				minPrice = Math.max(minPrice, 3500.00);
				maxPrice = Math.min(maxPrice, 6000.00);
			}
			if (price.contains("above6000")) {
				minPrice = Math.max(minPrice, 6000.00);
			}
		}

		List<Gender> selectedGenders = (gender != null && !gender.isEmpty())
				? gender.stream().map(Gender::valueOf).collect(Collectors.toList())
				: List.of();
		List<Product> fetchedProducts;

		List<Integer> categoryId = category != null ? category : List.of();
		List<Integer> sizeId = size != null ? size : List.of();

		if (!selectedGenders.isEmpty() && categoryId != null && !categoryId.isEmpty() && sizeId != null
				&& !sizeId.isEmpty()) {
			fetchedProducts = productService.getProductsByGenderPriceCategoryIdSizeId(selectedGenders, category, size,
					minPrice, maxPrice);
		} 
		else if (!selectedGenders.isEmpty() && categoryId != null && !categoryId.isEmpty()) {
			fetchedProducts = productService.getProductsByGenderInAndCategoryIn(selectedGenders, category);
		} 
		else if (!selectedGenders.isEmpty()) {
			fetchedProducts = productService.getProductsByGenderInAndPriceBetween(selectedGenders, minPrice, maxPrice);
		}
		else if (category != null && !category.isEmpty() && size != null && !size.isEmpty()) {
			fetchedProducts = productService.getProductsByCategoryIdSizeIdAndPriceBetween(category, size, minPrice,
					maxPrice);
		}
		else {
			fetchedProducts = productService.getProductsByPriceBetween(minPrice, maxPrice);
		}

		return ResponseEntity.ok(fetchedProducts);
	}
}
