package com.revshop.wishlist_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.revshop.wishlist_service.entity.Product;
import com.revshop.wishlist_service.entity.UserModel;
import com.revshop.wishlist_service.entity.Wishlist;
import com.revshop.wishlist_service.service.WishlistService;

public class TestWishlistController {

	@InjectMocks
	private WishlistController wishlistController;
	
	@Mock
	private WishlistService wishlistService;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void testGetWishlistProducts() {
		int userId = 2;
		
		Product product1 = new Product();
		product1.setProductId(1);
		
        Product product2 = new Product();
        product2.setProductId(2);
        
	    List<Product> products = Arrays.asList(product1, product2);
	    
	    when(wishlistService.getWishlist(userId)).thenReturn(products);
	   	    
	    ResponseEntity<?> response = wishlistController.getProducts(userId);	    
	    assertEquals(HttpStatus.OK, response.getStatusCode());   
	}
	
	@Test
	public void testGetWishlistProducts_ReturnsBadRequest() {
		int userId = 2;
	    
	    when(wishlistService.getWishlist(userId)).thenReturn(null);
	   	    
	    ResponseEntity<?> response = wishlistController.getProducts(userId);	    
	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());   
	}
	
	@Test
	public void testAddProduct_ReturnsOk() {
		int userId = 2;
		int productId = 1;
		
		Product product = new Product();
		product.setProductId(productId);

		UserModel user = new UserModel();
		user.setUserId(userId);
		
		Wishlist wishlist = new Wishlist();
		wishlist.setProduct(product);
		wishlist.setUser(user);
		
		when(wishlistService.addToWishlist(userId, productId)).thenReturn(wishlist);
		when(wishlistService.isExistInWishlist(userId, productId)).thenReturn(false);
		ResponseEntity<?> response = wishlistController.addToWishlist(userId, productId);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
	@Test
	public void testAddProduct_ReturnsBadRequest() {
		int userId = 2;
		int productId = 1;
				
		when(wishlistService.addToWishlist(userId, productId)).thenReturn(null);
		when(wishlistService.isExistInWishlist(userId, productId)).thenReturn(false);
		ResponseEntity<?> response = wishlistController.addToWishlist(userId, productId);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testAddProduct_AlreadyExists_ReturnsBadRequest() {
		int userId = 2;
		int productId = 1;
				
		when(wishlistService.isExistInWishlist(userId, productId)).thenReturn(true);
		ResponseEntity<?> response = wishlistController.addToWishlist(userId, productId);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	public void testDeleteProduct() {
		int userId = 2;
		int productId = 1;
				
		ResponseEntity<?> response = wishlistController.removeProduct(userId, productId);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
	
}
