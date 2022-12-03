package com.product.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "product")
public class ProductDto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("product_id")
    @Column(name = "product_id")
    private int product_id;
    
    @JsonProperty("gtin")
    @Column(name = "gtin")
    @NotNull(message = "gtin is required")
    private String gtin;
    
    @JsonProperty("product")
    @Column(name = "product")
    @NotNull(message = "product is required")
    private String product;
    
    @JsonProperty("price")
    @Column(name = "price")
    @NotNull(message = "price is required")
    private double price;
    
    @JsonIgnore
    @Column(name = "category_id")
    @NotNull(message = "category_id is required")
    private Integer category_id;
    
    
    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    } 
}