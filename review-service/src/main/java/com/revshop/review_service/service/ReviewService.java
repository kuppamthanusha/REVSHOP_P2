package com.revshop.review_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revshop.review_service.entity.Review;
import com.revshop.review_service.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

//    private static final String REVIEW_SERVICE = "reviewService";

//    @CircuitBreaker(name = REVIEW_SERVICE, fallbackMethod = "fallbackAddReview")
    public Review addReview(Review review) {
        return reviewRepository.save(review); 
    }
    
    public List<Review> getReviews(Integer productId){
    	return reviewRepository.findByProductProductId(productId);
    }

//    public void fallbackAddReview(Review review, Throwable throwable) {
//        System.out.println("Fallback method executed due to: " + throwable.getMessage());
//
//    }
}

