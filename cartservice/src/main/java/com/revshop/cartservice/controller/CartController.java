package com.revshop.cartservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revshop.cartservice.entity.*;
import com.revshop.cartservice.service.*;


@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	@PostMapping("/get")
	public ResponseEntity<List<Cart>> showCartPage(@RequestBody UserModel userModel) {
		List<Cart> cartItems = cartService.getCartItemsByuserModel(userModel);
		if(cartItems == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(cartItems);
	}

	@PostMapping("/add")
	public ResponseEntity<?> addToCart(@RequestBody Cart cart) {
		Cart cartData = cartService.addToCart(cart);
		if(cartData == null) {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(200);
	}

	@GetMapping("/delete")
	public ResponseEntity<?> deleteFromCart(@RequestParam("productId") Integer productId, @RequestParam("userId") Integer userId) {
		Cart cart = cartService.findByUserAndProduct(userId, productId);
		if(cart == null) {
			return ResponseEntity.notFound().build();
		}
		cartService.deleteFromCart(cart);
		return ResponseEntity.ok(200);
	}

	@GetMapping("/update")
	public ResponseEntity<?> updateQuantity(@RequestParam("productId") Integer productId, @RequestParam("userId") Integer userId,
			@RequestParam("quantity") Integer quantity) {
		Cart cart = cartService.findByUserAndProduct(userId, productId);
		if(cart == null) {
			return ResponseEntity.notFound().build();
		}
		if (quantity > 0) {
			cart.setQuantity(quantity);
			Cart cart1 = cartService.updateCart(cart);
			if(cart1 == null) {
				return ResponseEntity.badRequest().build();
			}
		} else {
			cartService.deleteFromCart(cart);
		}
		return ResponseEntity.ok(200);
	}
}
