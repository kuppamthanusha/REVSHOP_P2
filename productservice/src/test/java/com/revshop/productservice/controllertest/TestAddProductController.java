package com.revshop.productservice.controllertest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.revshop.productservice.controller.AddProductController;
import com.revshop.productservice.entity.Category;
import com.revshop.productservice.entity.Product;
import com.revshop.productservice.entity.SellerModel;
import com.revshop.productservice.entity.Size;
import com.revshop.productservice.service.ProductService;

public class TestAddProductController {

    @InjectMocks
    private AddProductController addProductController;

    @Mock
    private ProductService productService;

    private Product product;
    private SellerModel sellerModel;
    private Size size;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setProductId(1);
        product.setName("Test Product");

        sellerModel = new SellerModel();
        sellerModel.setSellerId(1);

        size = new Size();
        size.setSizeId(1);
        size.setSizeName("M");
    }

    @Test
    public void testGetAllSizes() {
        int categoryId = 1;
        Category category = new Category(); 
        category.setCategoryId(categoryId);
        category.setCategoryName("Clothing");

        Size size1 = new Size();
        size1.setSizeId(1);
        size1.setSizeName("M");
        size1.setCategory(category);  

        Size size2 = new Size();
        size2.setSizeId(2);
        size2.setSizeName("L");
        size2.setCategory(category); 

        List<Size> sizes = Arrays.asList(size1, size2);

        when(productService.getAllSizes(categoryId)).thenReturn(sizes);

        ResponseEntity<?> response = addProductController.getAllSizes(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sizes, response.getBody());
        verify(productService, times(1)).getAllSizes(categoryId);
    }

    @Test
    public void testAddProductToInventory_Success() {
        int sizeId = 1;
        int sellerId = 1;
        when(productService.getSizeById(sizeId)).thenReturn(size);
        when(productService.getSellerById(sellerId)).thenReturn(sellerModel);

        ResponseEntity<String> response = addProductController.addProductToInventory(sizeId, sellerId, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Added product", response.getBody());
        verify(productService, times(1)).saveProduct(product);
    }

    @Test
    public void testAddProductToInventory_LoginRequired() {
        int sizeId = 1;
        int sellerId = 0;
        when(productService.getSizeById(sizeId)).thenReturn(size);

        ResponseEntity<String> response = addProductController.addProductToInventory(sizeId, sellerId, product);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login", response.getBody());
        verify(productService, never()).saveProduct(any(Product.class));
    }

    @Test
    public void testDeleteProduct_Success() {
        int productId = 1;
        int sellerId = 1;
        when(productService.getSellerById(sellerId)).thenReturn(sellerModel);

        ResponseEntity<String> response = addProductController.deleteProuctFromInventory(productId, sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("delete Product", response.getBody());
        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    public void testDeleteProduct_LoginRequired() {
        int productId = 1;
        int sellerId = 0;

        ResponseEntity<String> response = addProductController.deleteProuctFromInventory(productId, sellerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login", response.getBody());
        verify(productService, never()).deleteProduct(anyInt());
    }

    @Test
    public void testUpdateProduct_Success() {
        int sellerId = 1;
        int productId = 1;
        when(productService.getSellerById(sellerId)).thenReturn(sellerModel);
        when(productService.findById(productId)).thenReturn(product);

        ResponseEntity<String> response = addProductController.updateProductToInventory(product, sellerId, productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("product Updated", response.getBody());
        verify(productService, times(1)).saveProduct(product);
    }

    @Test
    public void testShowUpdateForm() {
        int productId = 1;
        when(productService.getProductDetails(productId)).thenReturn(product);

        ResponseEntity<Product> response = addProductController.showUpdateForm(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductDetails(productId);
    }
}

