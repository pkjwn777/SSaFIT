package com.ssafy.model.dao;


import org.springframework.scheduling.annotation.Scheduled;

import com.ssafy.model.dto.RefreshDTO;

public interface RefreshDao {
	public abstract Boolean existsByRefresh(String refresh);
	public abstract void deleteByRefresh(String refresh);
	public abstract void insertRefreshToken(RefreshDTO refreshDto);
}
