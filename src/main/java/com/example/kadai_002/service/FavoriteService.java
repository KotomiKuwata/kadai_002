package com.example.kadai_002.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai_002.entity.Favorite;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.repository.FavoriteRepository;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	@Transactional
	public void addFavorite(User user, Store store) {
		Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndStore(user, store);
		if (existingFavorite.isEmpty()) {
			Favorite favorite = new Favorite();
			favorite.setUser(user);
			favorite.setStore(store);
			favoriteRepository.save(favorite);
		}
	}

	@Transactional
	public void removeFavorite(User user, Store store) {
		favoriteRepository.deleteByUserAndStore(user, store);
	}

	public List<Favorite> getFavoritesByUser(User user) {
		return favoriteRepository.findByUser(user);
	}

	public boolean isFavorite(User user, Store store) {
        return favoriteRepository.findByUserAndStore(user, store).isPresent();
    }
}
