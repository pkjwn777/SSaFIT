package com.ssafy.model.service;

import java.util.List;

import com.ssafy.exception.VideoNotFoundException;
import com.ssafy.model.dto.Video;

public interface VideoService {
	public abstract Video selectOne(int youtubeKey);
	public abstract List<Video> searchBy(Video video);
	public abstract int deleteVideo(int videoKey);
	public abstract int deleteList(String videoChannelName);
	public abstract List<Video> orderBy(String orderKey);
	public abstract int addViewCnt(int videoKey);
}
