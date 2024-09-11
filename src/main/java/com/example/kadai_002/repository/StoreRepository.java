package com.example.kadai_002.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai_002.entity.Category;
import com.example.kadai_002.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	public Page<Store> findByNameLike(String keyword, Pageable pageable);
	 public List<Store> findByCategoryId(Integer categoryId);
	 public Page<Store> findByCategory_Name(String categoryName, Pageable pageable);
	 public Page<Store> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable); 
	 public List<Store> findTop10ByOrderByCreatedAtDesc();
	 boolean existsByCategory(Category category);
}