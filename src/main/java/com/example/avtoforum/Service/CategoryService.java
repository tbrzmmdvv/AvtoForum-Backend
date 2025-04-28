package com.example.avtoforum.Service;


import com.example.avtoforum.Model.Dto.RequestDTO.CategoryRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.CategoryResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.CategorySummaryResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

    MessageResponse deleteCategory(Long id);

    List<CategorySummaryResponse> getCategoriesWithTopicCount();
}
