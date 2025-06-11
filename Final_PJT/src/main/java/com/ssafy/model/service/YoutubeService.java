package com.ssafy.model.service;

import java.io.IOException;
import java.util.List;

import com.ssafy.model.dto.Video;

public interface YoutubeService {
	public abstract boolean registVideo(Video video);
	public abstract String searchByYoutubeId(String youtubeId) throws IOException;
	public abstract Video selectByTitle();
	public abstract Video selectByKey();
	public abstract List<Video> selectByFitPartName(String FitPartName);
	public abstract List<Video> selectByChannel();
	public abstract boolean updateVideo(Video video);
	public abstract boolean deleteVideo(int videoKey);
}
