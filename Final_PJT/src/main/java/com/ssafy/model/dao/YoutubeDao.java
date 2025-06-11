package com.ssafy.model.dao;

import com.ssafy.model.dto.Video;

public interface YoutubeDao {
	public abstract boolean insert(Video video);
	public abstract Video search(String key);
}
