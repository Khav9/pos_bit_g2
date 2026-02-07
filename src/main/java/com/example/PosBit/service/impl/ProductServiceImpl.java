package com.example.PosBit.service.impl;

import com.example.PosBit.exception.CloudVendorNotFoundException;
import com.example.PosBit.model.Product;
import com.example.PosBit.repository.ProductRepository;
import com.example.PosBit.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public String createProduct(Product product) {
        productRepository.save(product);
        return "Success";
    }

    @Override
    public String updateProduct(Product product) {
        productRepository.save(product);
        return "Success";
    }

    @Override
    public String deleteProduct(int productId) {
        productRepository.deleteById(productId);
        return "Success";
    }

    @Override
    public Product getProduct(int productId) {
        if (productRepository.findById(productId).isEmpty())
            throw new CloudVendorNotFoundException("Product not found");
        return productRepository.findById(productId).get();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
