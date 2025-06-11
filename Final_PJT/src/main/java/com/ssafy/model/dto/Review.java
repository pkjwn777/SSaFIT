package com.ssafy.model.dto;

public class Review {
	private int reviewKey; /** 전체 리뷰중에서의 번호 */
	private int userKey; /** Join 용 */
	private int videoKey; /** Join 용 */
	private String reviewTitle;
	private String reviewWriter;
	private String reviewContent;
	
	public Review(){}

	public Review(int reviewKey, int userKey, int videoKey, String reviewTitle, String reviewWriter,
			String reviewContent) {
		super();
		this.reviewKey = reviewKey;
		this.userKey = userKey;
		this.videoKey = videoKey;
		this.reviewTitle = reviewTitle;
		this.reviewWriter = reviewWriter;
		this.reviewContent = reviewContent;
	}

	public int getReviewKey() {
		return reviewKey;
	}

	public void setReviewKey(int reviewKey) {
		this.reviewKey = reviewKey;
	}

	public int getUserKey() {
		return userKey;
	}

	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}

	public int getVideoKey() {
		return videoKey;
	}

	public void setVideoKey(int videoKey) {
		this.videoKey = videoKey;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReviewWriter() {
		return reviewWriter;
	}

	public void setReviewWriter(String reviewWriter) {
		this.reviewWriter = reviewWriter;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	@Override
	public String toString() {
		return "Review [reviewKey=" + reviewKey + ", userKey=" + userKey + ", videoKey=" + videoKey + ", reviewTitle="
				+ reviewTitle + ", reviewWriter=" + reviewWriter + ", reviewContent=" + reviewContent + "]";
	}
}
