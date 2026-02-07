package com.example.PosBit.service.impl;

import com.example.PosBit.exception.CloudVendorNotFoundException;
import com.example.PosBit.model.Category;
import com.example.PosBit.repository.CategoryRepository;
import com.example.PosBit.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String createCategory(Category category) {
        categoryRepository.save(category);
        return "Success";
    }

    @Override
    public String updateCategory(Category category) {
        categoryRepository.save(category);
        return "Success";
    }

    @Override
    public String deleteCategory(String cloudVendorId) {
        categoryRepository.deleteById(cloudVendorId);
        return "Success";
    }

    @Override
    public Category getCategory(String categoryId) {
        if (categoryRepository.findById(categoryId).isEmpty())
            throw new CloudVendorNotFoundException("Category does not exist");
        return categoryRepository.findById(categoryId).get();

    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
