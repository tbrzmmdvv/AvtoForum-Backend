package com.example.avtoforum.Service;

import com.example.avtoforum.Exception.ResourceNotFoundException;
import com.example.avtoforum.Model.Dto.RequestDTO.CategoryRequest;
import com.example.avtoforum.Model.Dto.ResponseDTO.CategoryResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.CategorySummaryResponse;
import com.example.avtoforum.Model.Dto.ResponseDTO.MessageResponse;
import com.example.avtoforum.Model.Entity.Category;
import com.example.avtoforum.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getCreatedAt(),
                        category.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        // Kategori adı kontrolü
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new RuntimeException("Bu kategori adı zaten kullanılıyor!");
        }

        // Yeni kategori oluştur
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getDescription(),
                savedCategory.getCreatedAt(),
                savedCategory.getUpdatedAt()
        );
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Kategori adı değişmiş ve bu ad başka kategoride kullanılıyorsa
        if (!category.getName().equals(categoryRequest.getName()) &&
                categoryRepository.existsByName(categoryRequest.getName())) {
            throw new RuntimeException("Bu kategori adı zaten kullanılıyor!");
        }

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getDescription(),
                updatedCategory.getCreatedAt(),
                updatedCategory.getUpdatedAt()
        );
    }

    @Override
    public MessageResponse deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);

        return new MessageResponse("Kategori başarıyla silindi!");
    }

    @Override
    public List<CategorySummaryResponse> getCategoriesWithTopicCount() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> new CategorySummaryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getTopics().size()
                ))
                .collect(Collectors.toList());
    }
}
