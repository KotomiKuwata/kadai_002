package com.example.kadai_002.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai_002.entity.Review;
import com.example.kadai_002.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	Page<Review> findByUserOrderByTimestampDesc(User user, Pageable pageable);
	//Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<Review> findAll();
    List<Review> findByStore_Id(Integer storeId);
    Page<Review> findByUser_IdOrderByTimestampDesc(Integer userId, Pageable pageable);
    List<Review> findByStoreId(Integer storeId);
}