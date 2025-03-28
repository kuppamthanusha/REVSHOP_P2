package com.revshop.review_service.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="products")
public class Product {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

	@Column(name = "name", nullable = false, length = 150)
    private String name;
     
    @Column(name = "description")
    private String description;
    
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    @Column(name = "price", nullable = false)
    private double price;
    
    @Column(name = "discount_price")
    private Double discountPrice;
    
    @Column(name = "quantity_available", nullable = false)
    private int quantityAvailable;
    
    @Column(name = "threshold_quantity", nullable = false)
    private int thresholdQuantity = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
 
    @OneToMany(mappedBy = "product", cascade=CascadeType.ALL)
    @JsonManagedReference
    private List<Review> reviews;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    public void setThresholdQuantity(int thresholdQuantity) {
        this.thresholdQuantity = thresholdQuantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

}