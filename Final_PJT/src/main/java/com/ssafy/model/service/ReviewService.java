package com.ssafy.model.service;

import java.util.List;

import com.ssafy.model.dto.Review;

public interface ReviewService {
	boolean insertReview(Review review);
	Review readOnlyReview(int reviewKey);
	List<Review> readOneVideoReview(int videoKey);
	boolean updateReview(Review review);
	boolean deleteOne(int reviewKey);
	boolean deleteAll(int videoKey);
	
	Review getReviewByReviewKey(int reviewKey);
}
