package com.revshop.productservice.controllertest;

import com.revshop.productservice.controller.ProductController;
import com.revshop.productservice.entity.Category;
import com.revshop.productservice.entity.Product;
import com.revshop.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestProductController {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testViewProductsPage() {
 
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setName("Product 1");
        products.add(product1);

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setName("Product 2");
        products.add(product2);

        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<?> response = productController.viewProductsPage();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(products, response.getBody());
        verify(productService, times(1)).getAllProducts();
    }

    @SuppressWarnings("deprecation")
	@Test
    public void testViewProductsCats() {
        List<Category> categories = new ArrayList<>();
        Category category1 = new Category();
        category1.setCategoryId(1);
        category1.setCategoryName("Category 1");
        categories.add(category1);

        when(productService.getAllCategories()).thenReturn(categories);

        ResponseEntity<?> response = productController.viewProductsCats();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(categories, response.getBody());
        verify(productService, times(1)).getAllCategories();
    }

    @Test
    public void testGetProductById() {
        Product product = new Product();
        product.setProductId(1);
        product.setName("Test Product");

        when(productService.findById(1)).thenReturn(product);

        ResponseEntity<?> response = productController.getProductById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).findById(1);
    }

    @Test
    public void testGetProductById_ProductNotFound() {
        when(productService.findById(1)).thenReturn(null);

        ResponseEntity<?> response = productController.getProductById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
        verify(productService, times(1)).findById(1);
    }

    @Test
    public void testShowProductsBySeller() {
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setName("Seller Product 1");
        products.add(product1);

        when(productService.getProductsBySeller(1)).thenReturn(products);

        ResponseEntity<?> response = productController.showProducts(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(products, response.getBody());
        verify(productService, times(1)).getProductsBySeller(1);
    }

    @Test
    public void testShowProductsBySeller_NoProducts() {
    
        when(productService.getProductsBySeller(1)).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = productController.showProducts(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new ArrayList<>(), response.getBody());
        verify(productService, times(1)).getProductsBySeller(1);
    }
}

