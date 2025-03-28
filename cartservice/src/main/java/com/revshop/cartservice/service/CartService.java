package com.revshop.cartservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshop.cartservice.entity.*;
import com.revshop.cartservice.repository.*;

@Service
public class CartService {
	
	@Autowired
    private CartRepository cartRepository;

    public Cart addToCart(Cart cart) {
        UserModel user = cart.getUser();
        Product product = cart.getProduct();

        Optional<Cart> existingCartItem = cartRepository.findByUserModelAndProduct(user, product);

        if (existingCartItem.isPresent()) {
            Cart updatedCart = existingCartItem.get();
            updatedCart.setQuantity(updatedCart.getQuantity() + cart.getQuantity());
            return cartRepository.save(updatedCart);
        } else {
            return cartRepository.save(cart);
        }
    }


    public List<Cart> getCartItemsByuserModel(UserModel userModel) {
        return cartRepository.findByUserModel(userModel);
    }
  
    @Transactional
    public void clearCart(UserModel user) {
        cartRepository.deleteByUserModel(user);
    }

    public Cart findByUserAndProduct(int userId, int productId) {
      Cart cart = cartRepository.findByUserModelUserIdAndProductProductId(userId, productId);
      return cart;
    }

    public void deleteFromCart(Cart cart) {
      cartRepository.delete(cart);
    }

    public Cart updateCart(Cart cart) {
      return cartRepository.save(cart);
    }
}
