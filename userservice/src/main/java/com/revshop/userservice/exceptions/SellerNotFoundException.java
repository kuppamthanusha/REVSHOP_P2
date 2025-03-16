package com.revshop.userservice.exceptions;

public  class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(String message) {
        super(message);
    }

}