package com.ssafy.model.service;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.exception.VideoNotFoundException;
import com.ssafy.model.dao.UserDao;
import com.ssafy.model.dao.VideoDao;
import com.ssafy.model.dto.Video;

@Service
public class VideoServiceImpl implements VideoService {
	
	private final VideoDao videoDao;
	
	public VideoServiceImpl(VideoDao videoDao){
		this.videoDao = videoDao;
	}
	
	@Override
	public Video selectOne(int youtubeKey){
		return videoDao.selectOne(youtubeKey);
	}

	@Override
	public List<Video> searchBy(Video video) {
		return videoDao.searchBy(video);
	}

	@Override
	public int deleteVideo(int videoKey) {
		return videoDao.deleteOne(videoKey);
	}

	@Override
	public int deleteList(String videoChannelName) {
		return videoDao.deleteList(videoChannelName);
	}

	@Override
	public List<Video> orderBy(String orderKey) {
		return videoDao.orderBy(orderKey);
	}

	@Override
	public int addViewCnt(int videoKey) {
		return videoDao.addViewCnt(videoKey);
		
	}
}
