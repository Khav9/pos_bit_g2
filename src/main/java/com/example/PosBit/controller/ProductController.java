package com.example.PosBit.controller;

import com.example.PosBit.model.Category;
import com.example.PosBit.model.Product;
import com.example.PosBit.response.ResponeHandler;
import com.example.PosBit.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    ProductService  productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Object> getProductDetails(@PathVariable Integer productId) {
        return ResponeHandler.responseBuilder("Requested product details are given here", HttpStatus.OK, productService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<Object> getAllProductDetails() {
        return ResponeHandler.responseBuilder(
                "All products details are given here",
                HttpStatus.OK,
                productService.getAllProducts()
        );
    }

    @PostMapping
    public String createProductDetails(@RequestBody Product  product) {
        productService.createProduct(product);
        return "Product created successfully";
    }

    @PutMapping
    public String updateProductDetails(@RequestBody Product  product) {
        productService.updateProduct(product);
        return "Product updated successfully";
    }

    @DeleteMapping("/{productId}")
    public String deleteProductDetails(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return "Product deleted successfully";
    }
}
