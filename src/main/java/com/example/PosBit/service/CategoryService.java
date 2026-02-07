package com.example.PosBit.service;

import com.example.PosBit.model.Category;

import java.util.List;

public interface CategoryService {
    public String createCategory(Category category);
    public String updateCategory(Category category);
    public String deleteCategory(String categoryId);
    public Category getCategory(String categoryId);
    public List<Category> getAllCategories();
}
