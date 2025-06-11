package com.ssafy.model.dao;

import java.util.List;

import com.ssafy.model.dto.Video;

public interface S3Dao {
	int insertVideo(Video video);
	List<Video> selectByTitle(String title);
	Video selectByKey(int videoKey);
	List<Video> selectByFitPartName(String fitPartName);
	List<Video> selectByChannelName(String videoTitle);
	int updateVideo(Video video);
	int deleteVideo(int videoKey);
	int setDatabaseLink(Video video);
}
