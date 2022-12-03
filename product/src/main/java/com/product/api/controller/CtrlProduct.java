package com.product.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.ProductDto;
import com.product.api.entity.Product;
import com.product.api.service.SvcProduct;
import com.product.exception.ApiException;

@RestController
@RequestMapping("/product")
public class CtrlProduct {

	@Autowired
	SvcProduct svc;
	
    @GetMapping("/{gtin}")
	public ResponseEntity<Product> getProduct(@PathVariable("gtin") String gtin) {
		return new ResponseEntity<>(svc.getProduct(gtin), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody Product in, BindingResult bindingResult){
		if(bindingResult.hasErrors())
			throw new ApiException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
		return new ResponseEntity<>(svc.createProduct(in),HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") Integer id, @Valid @RequestBody Product in, BindingResult bindingResult){
		if(bindingResult.hasErrors())
			throw new ApiException(HttpStatus.BAD_REQUEST, bindingResult.getAllErrors().get(0).getDefaultMessage());
		return new ResponseEntity<>(svc.updateProduct(in, id),HttpStatus.OK);
	}
	
    @PutMapping(path = "/{gtin}/stock/{quantity}")
	ResponseEntity<ApiResponse> updateProductStock(@PathVariable(value = "gtin") String gtin, @PathVariable(value = "quantity") int quantity) {
		try {
			ApiResponse response = svc.updateProductStock(gtin, quantity);
		}catch(DataIntegrityViolationException e) {
			if(e.getLocalizedMessage().contains("stock"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "we have not enough stock");
		}
		return new ResponseEntity<>(new ApiResponse("stock updated"), HttpStatus.OK);
	}

    @DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("id") Integer id){
		return new ResponseEntity<>(svc.deleteProduct(id), HttpStatus.OK);
	}

	@GetMapping(path = "/category/{category_id}")
    ResponseEntity<List<ProductDto>> listProducts(@PathVariable(value = "category_id") int categoryId) throws Exception{
        List<ProductDto> listProducts = svc.listProducts(categoryId);

        return new ResponseEntity<>(listProducts, HttpStatus.OK);
    }

    @PutMapping(path = "/{gtin}/category")
    ResponseEntity<ApiResponse> updateProductCategory(@PathVariable(value = "gtin") String gtin, @RequestBody Product product) {
        try {
            ApiResponse response = svc.updateProductCategory(gtin, product.getCategory_id());
        } catch (DataIntegrityViolationException e) {
            if(e.getLocalizedMessage().contains("gtin"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
            if(e.getLocalizedMessage().contains("product"))
                throw new ApiException(HttpStatus.BAD_GATEWAY, "product name already exist");
        }
        return new ResponseEntity<>(new ApiResponse("product category updated"), HttpStatus.OK);
    }
}
