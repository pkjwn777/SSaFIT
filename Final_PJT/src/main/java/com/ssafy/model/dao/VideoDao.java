package com.ssafy.model.dao;

import java.util.List;

import com.ssafy.model.dto.Video;

public interface VideoDao {
	public abstract Video selectOne(int videoKey);
	public abstract boolean update(Video video);
	//public abstract Video search(String key);
	public abstract List<Video> searchBy(Video video);
	public abstract int deleteOne(int videoKey);
	public abstract int deleteList(String videoChannelName);
	public abstract List<Video> orderBy(String orderKey);
	public abstract int addViewCnt(int videoKey);
}
