package com.revshop.userservice.controllertest;

import com.revshop.userservice.controller.LoginController;
import com.revshop.userservice.dto.LoginResponse;
import com.revshop.userservice.entity.LoginModel;
import com.revshop.userservice.entity.SellerModel;
import com.revshop.userservice.entity.UserModel;
//import com.revshop.userservice.service.MessageProducer;
import com.revshop.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private UserService userService;
    
//    @Mock
//    private MessageProducer messageProducer;

    private LoginModel loginModel;
    private UserModel userModel;
    private SellerModel sellerModel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        loginModel = new LoginModel();
        loginModel.setUserEmail("test@example.com");
        loginModel.setUserPassword("password");

        userModel = new UserModel();
        userModel.setUserId(1);
        userModel.setUserRole("seller");

        sellerModel = new SellerModel();
        sellerModel.setSellerId(1);
    }

    @Test
    public void testProcessLogin_SuccessfulLogin_AsSeller() {
        when(userService.findByEmail("test@example.com")).thenReturn(userModel);
        when(userService.isPasswordCorrect(userModel, "password")).thenReturn(true);
        when(userService.getSellerId(1)).thenReturn(sellerModel);

        ResponseEntity<?> response = loginController.processLogin(loginModel);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        assertEquals("seller", loginResponse.getRole());
        assertEquals(1, loginResponse.getId());
//        verify(messageProducer, times(1)).sendMessage(eq(loginResponse));
    }

    @Test
    public void testProcessLogin_UnknownUserRole() {
        userModel.setUserRole("admin");
        when(userService.findByEmail("test@example.com")).thenReturn(userModel);
        when(userService.isPasswordCorrect(userModel, "password")).thenReturn(true);

        ResponseEntity<?> response = loginController.processLogin(loginModel);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown user role.", response.getBody());
    }

    @Test
    public void testGetUserModel_Success() {
        when(userService.getUserId(1)).thenReturn(userModel);

        ResponseEntity<?> response = loginController.getUserModel(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userModel, response.getBody());
//        verify(messageProducer, times(1)).sendMessage(eq(userModel));
    }
}
