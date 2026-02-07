package com.example.PosBit.service;

import com.example.PosBit.model.Product;

import java.util.List;

public interface ProductService {
    public String createProduct(Product product);
    public String updateProduct(Product product);
    public String deleteProduct(int productId);
    public Product getProduct(int productId);
    public List<Product> getAllProducts();
}
