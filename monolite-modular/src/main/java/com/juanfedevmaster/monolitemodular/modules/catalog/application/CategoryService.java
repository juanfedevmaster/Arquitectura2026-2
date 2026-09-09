package com.juanfedevmaster.monolitemodular.modules.catalog.application;

import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.CategoryRequest;
import com.juanfedevmaster.monolitemodular.modules.catalog.api.dto.CategoryResponse;
import com.juanfedevmaster.monolitemodular.modules.catalog.domain.Category;
import com.juanfedevmaster.monolitemodular.modules.catalog.infrastructure.CategoryRepository;
import com.juanfedevmaster.monolitemodular.shared.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (repository.existsByName(request.name())) {
            throw new BusinessException("Category already exists: " + request.name());
        }
        Category category = Category.builder()
            .name(request.name())
            .description(request.description())
            .build();
        return toResponse(repository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOrThrow(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(repository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        repository.deleteById(id);
    }

    private Category getOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
