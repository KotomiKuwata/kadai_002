package com.example.kadai_002.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kadai_002.entity.Favorite;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;


public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	
	// ユーザーIDとストアIDに基づいてお気に入りを検索
    Optional<Favorite> findByUserAndStore(User user, Store store);
    
 // ユーザーIDとストアIDに基づいてお気に入りを削除
    void deleteByUserAndStore(User user, Store store);

	List<Favorite> findByUser(User user);

}
