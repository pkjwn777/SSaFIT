package com.ssafy.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.model.dto.Video;

public interface S3Service {
	public abstract boolean registVideo(Video video, MultipartFile multipartFile);
	public abstract List<Video> selectByTitle(String title);
	public abstract Video selectByKey(int videoKey);
	public abstract List<Video> selectByFitPartName(String fitPartName);
	public abstract List<Video> selectByChannelName(String channelName);
	public abstract boolean updateVideo(Video video);
	public abstract boolean deleteVideo(int videoKey, String title);
	
	public String generateThumbnail(String s3VideoKey);
    public abstract void copyVideo(String sourceKey, String destinationKey);
    public abstract void deleteFileOnlyFromS3(String s3Key);
}
