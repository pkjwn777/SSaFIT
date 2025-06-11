package com.ssafy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ssafy.Youtube.YoutubeUtils;
import com.ssafy.model.dao.VideoDao;
import com.ssafy.model.dao.YoutubeDao;
import com.ssafy.model.service.YoutubeServiceImpl;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class YoutubeConfig {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Bean
    public YouTube youtube() throws Exception {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {}
        ).setApplicationName("ssafy-video-service").build();
    }

    @Bean
    public YoutubeUtils youtubeUtils() {
        return new YoutubeUtils(); // static 아니고 인스턴스 메서드로 변경 필요
    }
    
    @Bean
    public YoutubeServiceImpl youtubeService(YouTube youtube, YoutubeUtils youtubeUtils, YoutubeDao youtubeDao, VideoDao videoDao) {
        return new YoutubeServiceImpl(apiKey, youtube, youtubeUtils, youtubeDao, videoDao);
    }
}