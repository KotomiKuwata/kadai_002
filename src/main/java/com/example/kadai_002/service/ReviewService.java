package com.example.kadai_002.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai_002.entity.Review;
import com.example.kadai_002.entity.Store;
import com.example.kadai_002.entity.User;
import com.example.kadai_002.form.ReviewInputForm;
import com.example.kadai_002.repository.ReviewRepository;
import com.example.kadai_002.repository.StoreRepository;
import com.example.kadai_002.repository.UserRepository;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;

	public ReviewService(ReviewRepository reviewRepository, StoreRepository storeRepository,
			UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void create(ReviewInputForm reviewInputForm) {
		try {
			if (reviewInputForm.getStoreId() == null || reviewInputForm.getUserId() == null) {
				throw new IllegalArgumentException("Store ID and User ID must not be null");
			}
			Review review = new Review();
			Store store = storeRepository.getReferenceById(reviewInputForm.getStoreId());
			User user = userRepository.getReferenceById(reviewInputForm.getUserId());

			review.setStore(store);
			review.setUser(user);
			review.setRating(reviewInputForm.getRating());
			review.setComment(reviewInputForm.getComment());
			review.setTimestamp(LocalDateTime.now());

			validateReview(review);
			reviewRepository.save(review);
			System.out.println("Review saved successfully: " + review);
		} catch (Exception e) {
			System.err.println("Error occurred while saving review: " + e.getMessage());
			throw e; // 再スローしてトランザクションをロールバック
		}
	}

	@Transactional
	public void update(Integer id, ReviewInputForm reviewInputForm) {
		try {
			Review review = reviewRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + id));

			review.setRating(reviewInputForm.getRating());
			review.setComment(reviewInputForm.getComment());

			validateReview(review);
			reviewRepository.save(review);
			System.out.println("Review updated successfully: " + review);
		} catch (Exception e) {
			System.err.println("Error occurred while updating review: " + e.getMessage());
			throw e; // 再スローしてトランザクションをロールバック
		}
	}

	@Transactional
	public void delete(Integer id) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + id));
		reviewRepository.delete(review);
	}

	public void validateReview(Review review) {
		if (review.getRating() < 1 || review.getRating() > 5) {
			throw new IllegalArgumentException("評価は1から5の間で入力してください。");
		}
		if (review.getComment() == null || review.getComment().trim().isEmpty()) {
			throw new IllegalArgumentException("コメントを入力してください。");
		}
		if (review.getComment().length() > 200) {
			throw new IllegalArgumentException("コメントは200文字以内で入力してください。");
		}
	}
}

/* 

 
 @Transactional

    public void create(ReviewRegisterForm reviewRegisterForm) {
        Review review = new Review();
        //Store store = storeRepository.getReferenceById(reviewRegisterForm.getStoreId());
        //User user = userRepository.getReferenceById(reviewRegisterForm.getUserId());
        Integer rating = reviewRegisterForm.getRating();
        String comment = reviewRegisterForm.getComment();

        review.setRating(rating);
        review.setComment(comment);
        review.setTimestamp(LocalDateTime.now());

        reviewRepository.save(review);
    }
    
     public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

   
    
    public List<Review> getReviewsByStoreId(Integer storeId) {
        return reviewRepository.findByStoreId(storeId);
    }
    
*/