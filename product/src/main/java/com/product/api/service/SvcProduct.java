package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.ProductDto;
import com.product.api.entity.Product;

public interface SvcProduct {

	public Product getProduct(String gtin);
	public ApiResponse createProduct(Product in);
	public ApiResponse updateProduct(Product in, Integer id);
	public ApiResponse updateProductStock(String gtin, Integer stock);
	public ApiResponse deleteProduct(Integer id);
	public ApiResponse updateProductCategory(String gtin, Integer category_id);
    public List<ProductDto> listProducts(Integer categoryId);
}
