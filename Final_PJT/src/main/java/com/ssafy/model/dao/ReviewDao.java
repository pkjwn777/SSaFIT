package com.ssafy.model.dao;

import java.util.List;

import com.ssafy.model.dto.Review;

public interface ReviewDao {
	int createReview(Review review);
	Review readOneReview(int reviewKey);
	List<Review> readAllReview(int videoKey);
	int updateReview(Review review);
	int deleteOneReview(int reviewKey);
	int deleteAllReview(int videoKey);
	
	Review getReviewByReviewKey(int reviewKey);
}
