package com.ssafy.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.model.dao.ReviewDao;
import com.ssafy.model.dto.Review;

@Service
public class ReviewServiceImpl implements ReviewService {

	private ReviewDao reviewDao;
	
	public ReviewServiceImpl(ReviewDao reviewDao)
	{
		this.reviewDao = reviewDao;
	}
	
	@Override
	public boolean insertReview(Review review) {
		return reviewDao.createReview(review) == 1;
	}

	@Override
	public Review readOnlyReview(int reviewKey) {
		return reviewDao.readOneReview(reviewKey);
	}

	@Override
	public List<Review> readOneVideoReview(int videoKey) {
		return reviewDao.readAllReview(videoKey);
	}

	@Override
	public boolean updateReview(Review review) {
		return reviewDao.updateReview(review) == 1;
	}

	@Override
	public boolean deleteOne(int reviewKey) {
		return reviewDao.deleteOneReview(reviewKey) == 1;
	}

	@Override
	public boolean deleteAll(int videoKey) {
		return reviewDao.deleteAllReview(videoKey) == 1;
	}


	public Review getReviewByReviewKey(int reviewKey)
	{
		return reviewDao.getReviewByReviewKey(reviewKey);
	}
}
