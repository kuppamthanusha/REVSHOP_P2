package com.revshop.productservice.controllertest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.revshop.productservice.controller.FilterProductController;
import com.revshop.productservice.entity.Category;
import com.revshop.productservice.entity.Size;
import com.revshop.productservice.service.ProductService;


public class TestFilterController {

	@InjectMocks
	private FilterProductController filterController;
	
	@Mock
	private ProductService productService;
	
	@BeforeEach
	 public void setUp() {
        MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void filterProducts() {
		int categoryId1 = 1;
        Category category1 = new Category(); 
        category1.setCategoryId(categoryId1);
        category1.setCategoryName("Shirts");
        
        int categoryId2 = 2;
        Category category2 = new Category();
        category1.setCategoryId(categoryId2);
        category1.setCategoryName("Shoes");
        
        Size size1 = new Size();
        size1.setSizeId(1);
        size1.setSizeName("M");
        size1.setCategory(category1);
        
        Size size2 = new Size();
        size2.setSizeId(2);
        size2.setSizeName("UK8");
        size2.setCategory(category2);
        
        List<Integer> categories = Arrays.asList(category1.getCategoryId(), category2.getCategoryId());
        List<Integer> sizes = Arrays.asList(size1.getSizeId(), size2.getSizeId());
        
        ResponseEntity<?> response = filterController.filterProducts(null, categories, sizes, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}


