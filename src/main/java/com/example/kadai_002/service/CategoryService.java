package com.example.kadai_002.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.form.CategoryEditForm;
import com.example.kadai_002.form.CategoryRegisterForm;
import com.example.kadai_002.repository.CategoryRepository;
import com.example.kadai_002.repository.StoreRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final StoreRepository storeRepository;

	public CategoryService(StoreRepository storeRepository, CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
		this.storeRepository = storeRepository;
	}

	@Transactional
	public void create(CategoryRegisterForm categoryRegisterForm) {
		Category category = new Category();

		category.setName(categoryRegisterForm.getName());

		categoryRepository.save(category);
	}

	@Transactional
	public void update(CategoryEditForm categoryEditForm) {
		Category category = categoryRepository.getReferenceById(categoryEditForm.getId());

		category.setName(categoryEditForm.getName());

		categoryRepository.save(category);
	}

	@Transactional
	public void deleteById(Integer id) {
		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "カテゴリが見つかりません"));

		if (storeRepository.existsByCategory(category)) {
			throw new DataIntegrityViolationException("このカテゴリは使用中のため削除できません");
		}

		categoryRepository.delete(category);
	}
}
