package com.ssafy.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.model.dto.Video;
import com.ssafy.model.service.ReviewService;
import com.ssafy.model.service.UserService;
import com.ssafy.model.service.VideoService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/video")
public class VideoController {
	private final VideoService videoService;
	
	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}
	
	/**		read	 **/
	//프론트 단에서 검색 조건에 맞게 검색란에 입력 받는 값을 매핑 + 객체로 전달
	@PostMapping("/searchList")
	public ResponseEntity<List<Video>> searchList(@RequestBody Video video) {
		List<Video> result = videoService.searchBy(video);
		return ResponseEntity.ok(result);
	}
	//searchOne은 비디오 상세 페이지 등을 위해 존재 -> videoKey로만 진행
	@GetMapping("/searchOne/{videoKey}")
	public ResponseEntity<Video> searchOne(@PathVariable int videoKey) {
		Video video = videoService.selectOne(videoKey);
		videoService.addViewCnt(videoKey);
		if(video != null) {
			return ResponseEntity.ok(video);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/orderBy/{orderKey}")
	public ResponseEntity<?> orderBy(@PathVariable String orderKey) {
		List<Video> videoList = videoService.orderBy(orderKey);
		if(videoList != null) {
			return ResponseEntity.ok(videoList);
		}else {
			return new ResponseEntity<String>(HttpStatusCode.valueOf(403));
		}
	}
}
