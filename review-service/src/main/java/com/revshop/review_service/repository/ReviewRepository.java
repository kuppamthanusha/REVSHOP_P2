package com.revshop.review_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revshop.review_service.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	List<Review> findByProductProductId(Integer productId);
}
