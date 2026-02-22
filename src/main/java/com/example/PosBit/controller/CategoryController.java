package com.example.PosBit.controller;

import com.example.PosBit.model.Category;
import com.example.PosBit.response.ResponeHandler;
import com.example.PosBit.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/product-category")
public class CategoryController {
    CategoryService  categoryService;

    public CategoryController(CategoryService categoryService) {
        super();
        this.categoryService = categoryService;
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> getCategoryDetails(@PathVariable("categoryId") String categoryId) {
        return ResponeHandler.responseBuilder("Requested category details are given here", HttpStatus.OK, categoryService.getCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<Object> getAllCategoryDetails() {
        return ResponeHandler.responseBuilder(
                "All category details are given here",
                HttpStatus.OK,
                categoryService.getAllCategories()
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return "Category created successfully";
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable String categoryId, @RequestBody Category category) {
        category.setCategoryId(categoryId);
        categoryService.updateCategory(category);
        return "Category updated successfully";
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return "Category deleted successfully";
    }
}
