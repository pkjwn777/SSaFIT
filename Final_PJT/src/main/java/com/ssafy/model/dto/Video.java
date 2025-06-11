package com.ssafy.model.dto;

import java.util.ArrayList;
import java.util.List;


public class Video {
	private int videoKey; /** 우리가 가지고 있는 비디오 번호*/
	private int userKey;
	private String videoTitle;
	private String videoFitPartName;
	private String videoLink;
	private String videoChannelName;
	private int videoViewCnt;
	private char videoType; 
	
	// 리뷰 리스트는 빼둠
	private List<Review> reviewList = new ArrayList<>();
	
	public Video() {
		
	}
	
	public Video(int videoKey, int userKey, String videoTitle, String videoFitPartName, String videoLink,
			String videoChannelName, int videoViewCnt, char videoType, List<Review> reviewList) {
		super();
		this.videoKey = videoKey;
		this.userKey = userKey;
		this.videoTitle = videoTitle;
		this.videoFitPartName = videoFitPartName;
		this.videoLink = videoLink;
		this.videoChannelName = videoChannelName;
		this.videoViewCnt = videoViewCnt;
		this.videoType = videoType;
		this.reviewList = reviewList;
	}

	

	public int getVideoKey() {
		return videoKey;
	}

	public void setVideoKey(int videoKey) {
		this.videoKey = videoKey;
	}

	public int getUserKey() {
		return userKey;
	}

	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoFitPartName() {
		return videoFitPartName;
	}

	public void setVideoFitPartName(String videoFitPartName) {
		this.videoFitPartName = videoFitPartName;
	}

	public String getVideoLink() {
		return videoLink;
	}

	public void setVideoLink(String videoLink) {
		this.videoLink = videoLink;
	}

	public String getVideoChannelName() {
		return videoChannelName;
	}

	public void setVideoChannelName(String videoChannelName) {
		this.videoChannelName = videoChannelName;
	}

	public int getVideoViewCnt() {
		return videoViewCnt;
	}

	public void setVideoViewCnt(int videoViewCnt) {
		this.videoViewCnt = videoViewCnt;
	}

	public char getVideoType() {
		return videoType;
	}

	public void setVideoType(char videoType) {
		this.videoType = videoType;
	}

	public List<Review> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Review> reviewList) {
		this.reviewList = reviewList;
	}

	@Override
	public String toString() {
		return "Video [videoKey=" + videoKey + ", userKey=" + userKey + ", videoTitle=" + videoTitle
				+ ", videoFitPartName=" + videoFitPartName + ", videoLink=" + videoLink + ", videoChannelName="
				+ videoChannelName + ", videoViewCnt=" + videoViewCnt + ", videoType=" + videoType + ", reviewList="
				+ reviewList + "]";
	}

	
	
}
