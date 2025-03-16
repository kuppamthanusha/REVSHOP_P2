package com.revshop.review_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.revshop.review_service.entity.Review;
import com.revshop.review_service.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addReview(@RequestBody Review review) {
		Review res = reviewService.addReview(review);
		if(res != null) {			
			return ResponseEntity.ok(200);
		}
		return ResponseEntity.ok(500);
	}		
	
	@GetMapping("/get")
	public ResponseEntity<?> getAllReviews(@RequestParam("productId") Integer productId) {
		System.out.println("Product" + productId);
		List<Review> reviews = reviewService.getReviews(productId);
		return ResponseEntity.ok(reviews);
	}
}
	



