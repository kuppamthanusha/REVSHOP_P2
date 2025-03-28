package com.revshop.userservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revshop.userservice.entity.SellerModel;
import com.revshop.userservice.entity.UserModel;
import com.revshop.userservice.exceptions.PhoneNumberAlreadyExistsException;
import com.revshop.userservice.exceptions.UserAlreadyExistsException;
import com.revshop.userservice.service.UserService;

@RestController
@RequestMapping("/api/v1/register")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<String> processRegistration(@RequestBody UserModel userModel) {
        logger.info("Processing registration for user: {}", userModel.getUserEmail());
        
        UserModel existingUser = userService.findByEmail(userModel.getUserEmail());
        if (existingUser != null) {
            logger.warn("User with email {} already exists.", userModel.getUserEmail());
            throw new UserAlreadyExistsException("This email is already registered. Please use a different email or please login.");
        }

        UserModel existingUserWithMobileNumber = userService.findByPhoneNumber(userModel.getUserMobileNumber());
        if (existingUserWithMobileNumber != null) {
            logger.warn("User with phone number {} already exists.", userModel.getUserMobileNumber());
            throw new PhoneNumberAlreadyExistsException("Phone number exists.");
        }

        if ("seller".equals(userModel.getUserRole())) {
            SellerModel seller = userModel.getSellermodel();
            if (seller != null) {
                seller.setUsermodel(userModel);
                UserModel savedUser = userService.saveUser(userModel);
                seller.setUsermodel(savedUser);
                userService.saveSeller(seller);
                logger.info("Seller registered successfully: {}", seller);
            } else {
                logger.error("Seller model is null for user: {}", userModel.getUserEmail());
                return ResponseEntity.badRequest().body("Seller not found");
            }
        } else {
            userModel.setWalletBalance(5500.0);
            userService.saveUser(userModel);
            logger.info("User registered successfully: {}", userModel.getUserEmail());
        }

        return ResponseEntity.ok("User registered successfully");
    }
}

