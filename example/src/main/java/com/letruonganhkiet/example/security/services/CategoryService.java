package com.letruonganhkiet.example.security.services;

import com.letruonganhkiet.example.entity.Category;
import com.letruonganhkiet.example.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // ✅ Lấy tất cả danh mục
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    // ✅ Tạo mới danh mục
    public Category create(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    // ✅ Cập nhật danh mục theo ID
    public Category update(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setImageUrl(category.getImageUrl());
        existing.setUpdatedAt(LocalDateTime.now());

        if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
            Category parent = categoryRepository.findById(category.getParentCategory().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            existing.setParentCategory(parent);
        } else {
            existing.setParentCategory(null);
        }

        return categoryRepository.save(existing);
    }

    // ✅ Xóa danh mục theo ID
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    // ✅ Lấy danh mục theo ID
    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }
}