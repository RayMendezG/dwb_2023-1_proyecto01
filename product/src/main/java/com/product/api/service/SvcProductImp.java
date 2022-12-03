package com.product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.ProductDto;
import com.product.api.entity.Product;
import com.product.api.entity.Category;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import com.product.api.repository.RepoProductList;
import com.product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repoProduct;
	
	@Autowired
	RepoCategory repoCategory;

	@Autowired
	RepoProductList repoProductList;

	@Override
	public Product getProduct(String gtin) {
		Product product = repoProduct.findByProductGtin(gtin); // sustituir null por la llamada al método implementado en el repositorio
		if (product != null) {
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		} else {
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
		}
	}

	@Override
	public ApiResponse createProduct(Product in) {
        Category category = repoCategory.findByCategoryId(in.getCategory_id());        
        if(category != null) {
            Product product = repoProduct.findByGtin(in.getGtin());
            if(product != null) {
                updateProduct(in,product.getProduct_id());
                return new ApiResponse("product activated");
            }else {
                try {
                    in.setStatus(1);
                    repoProduct.save(in);
                }catch (DataIntegrityViolationException e) {
                    if (e.getLocalizedMessage().contains("gtin"))
                        throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
                    if (e.getLocalizedMessage().contains("product"))
                        throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
                }
                return new ApiResponse("Product created");
            }
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "Category not found");
		}
    }

	@Override
	public ApiResponse updateProduct(Product in, Integer id) {
		Integer updated = 0;
		try {
			updated = repoProduct.updateProduct(id, in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());
		}catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
			if (e.contains(SQLIntegrityConstraintViolationException.class))
				throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
		if (updated == 0)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be updated");
		else
			return new ApiResponse("product updated");
	}

	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repoProduct.deleteProduct(id) > 0)
			return new ApiResponse("product removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = getProduct(gtin);
		Integer currentStock= product.getStock(); 
		
		if(stock > product.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");
		repoProduct.updateProductStock(gtin, (currentStock - stock));
		return new ApiResponse("product stock updated");
	}

    @Override
    public ApiResponse updateProductCategory(String gtin, Integer category_id) {
        Product foundProduct = repoProduct.findByGtin(gtin);

        if(foundProduct== null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
        } else {
            if(foundProduct.getStatus() == 1) {
                if(repoCategory.findByCategoryId(category_id) == null) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
                }else {
                    if(foundProduct.getCategory_id() == category_id) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
                    }else {
                        repoProduct.updateProductCategory(gtin, category_id);
                        return new ApiResponse("product category updated");
                    }
                }
            } else {
                throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
            }
        }
    }

    @Override
    public List<ProductDto> listProducts(Integer categoryId) {
        return repoProductList.listProducts(categoryId);
    }
}
