package com.ssafy.model.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.ssafy.Youtube.YoutubeUtils;
import com.ssafy.model.dao.VideoDao;
import com.ssafy.model.dao.YoutubeDao;
import com.ssafy.model.dto.Video;

public class YoutubeServiceImpl implements YoutubeService{
	@Value("${youtube.api.key}")
	private final String apiKey;
    private final YouTube youtube;
    private final YoutubeUtils youtubeUtils;
    private final YoutubeDao youtubeDao;
    private final VideoDao videoDao;
	
    public YoutubeServiceImpl(String apiKey, YouTube youtube, YoutubeUtils youtubeUtils, YoutubeDao youtubeDao, VideoDao videoDao) {
        this.apiKey = apiKey;
        this.youtube = youtube;
        this.youtubeUtils = youtubeUtils;
        this.youtubeDao = youtubeDao;
        this.videoDao = videoDao;
    }
	
	@Override
	public boolean registVideo(Video video) {
		return youtubeDao.insert(video);
	}
	
	@Override
	public String searchByYoutubeId(String youtubeId) throws IOException {
		YouTube.Videos.List request = youtube.videos()
				.list("snippet")
				.setId(youtubeId)
				.setKey(apiKey);
		
		VideoListResponse response = request.execute();
		List<com.google.api.services.youtube.model.Video> videos = response.getItems();
		return videos.get(0).getSnippet().getTitle();
	}

	@Override
	public Video selectByTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Video selectByKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> selectByFitPartName(String FitPartName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> selectByChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateVideo(Video video) {
		return videoDao.update(video);
	}

	@Override
	public boolean deleteVideo(int videoKey) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
