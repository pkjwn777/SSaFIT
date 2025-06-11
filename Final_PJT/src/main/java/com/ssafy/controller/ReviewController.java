package com.ssafy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.model.dto.Review;
import com.ssafy.model.dto.Video;
import com.ssafy.model.service.ReviewService;
import com.ssafy.model.service.UserService;
import com.ssafy.model.service.VideoService;

@RestController
@RequestMapping("/review")
public class ReviewController {
	private final VideoService videoService;
	private final ReviewService reviewService;
	private final UserService userService;

	public ReviewController(VideoService videoService, ReviewService reviewService, UserService userService)
	{
		this.videoService = videoService;
		this.reviewService = reviewService;
		this.userService = userService;
	}
	
	@PostMapping("/insertReview/{videoKey}")
	public ResponseEntity<String> writeReview(@RequestBody Review review, @PathVariable("videoKey") int videoKey)
	{
		try {
			review.setVideoKey(videoKey);
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userId = authentication.getName();
			
			review.setReviewWriter(userId);
			int userKey = userService.getUserKeyByUserId(userId);
			review.setUserKey(userKey);
			
			reviewService.insertReview(review);
			return new ResponseEntity<String> ("Review written success.", HttpStatusCode.valueOf(200)); 
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String> ("Review write failed.", HttpStatusCode.valueOf(500)); 
		}
	}
	
	@GetMapping("/{reviewKey}")
	public ResponseEntity<Review> selectOneReview(@PathVariable("reviewKey") int reviewKey)
	{
		try {
			Review review = reviewService.readOnlyReview(reviewKey);
			return new ResponseEntity<Review> (review, HttpStatusCode.valueOf(200)); 
		} catch (Exception e) {
			return new ResponseEntity<Review> (HttpStatusCode.valueOf(500));
		}
	}
	
	@GetMapping("/videoReviews/{videoKey}")
	public ResponseEntity<List<Review>> selectAllReview(@PathVariable("videoKey") int videoKey)
	{
		try {
			List<Review> reviews = reviewService.readOneVideoReview(videoKey);
			return new ResponseEntity<List<Review>> (reviews, HttpStatusCode.valueOf(200)); 
		} catch (Exception e) {
			return new ResponseEntity<List<Review>> (HttpStatusCode.valueOf(500)); 
		}
	}
	
	@PutMapping("/update/{reviewKey}")
	public ResponseEntity<String> updateReview(@PathVariable("reviewKey") int reviewKey, @RequestBody Review review) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userId = authentication.getName();
			
			if(reviewService.readOnlyReview(reviewKey).getReviewWriter().equals(userId)) {
				// Video oldvideo = videoService.selectOne(reviewService.getVideoKeyByReviewKey(reviewKey));
				Review newReview = reviewService.getReviewByReviewKey(reviewKey);
				newReview.setReviewContent(review.getReviewContent());
				newReview.setReviewTitle(review.getReviewTitle());
				
				reviewService.updateReview(newReview);
			}
			else {
				return new ResponseEntity<String> ("Review update failed (not your review)" ,HttpStatusCode.valueOf(500));
			}
			
			return new ResponseEntity<String> ("Review updated", HttpStatusCode.valueOf(200)); 
		} catch (Exception e) {
			return new ResponseEntity<String> ("Review update failed (server side)" ,HttpStatusCode.valueOf(500)); 
		}
	}
	
	@DeleteMapping("/delete/{reviewKey}")
	public ResponseEntity<String> deleteReview(@PathVariable("reviewKey") int reviewKey) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userId = authentication.getName();
			
			if(reviewService.readOnlyReview(reviewKey).getReviewWriter().equals(userId)) {
				reviewService.deleteOne(reviewKey);
			}
			else {
				return new ResponseEntity<String> ("Review delete failed (not your review)" ,HttpStatusCode.valueOf(500));
			}
			
			return new ResponseEntity<String> ("Review deleted", HttpStatusCode.valueOf(200)); 
		} catch (Exception e) {
			return new ResponseEntity<String> ("Review delete failed (server side)" ,HttpStatusCode.valueOf(500)); 
		}
	}
}
