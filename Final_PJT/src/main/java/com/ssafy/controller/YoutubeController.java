package com.ssafy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.Youtube.YoutubeUtils;
import com.ssafy.model.dto.Video;
import com.ssafy.model.service.ReviewService;
import com.ssafy.model.service.UserService;
import com.ssafy.model.service.VideoService;
import com.ssafy.model.service.YoutubeService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/youtube")
public class YoutubeController {
	private final UserService userService;
	private final ReviewService reviewService;
	private final YoutubeService youtubeService;
	private final YoutubeUtils youtubeUtils;
	private final VideoService videoService;

	public YoutubeController(UserService userService, YoutubeService youtubeService, YoutubeUtils youtubeUtils,
			VideoService videoService, ReviewService reviewService) {
		this.userService = userService;
		this.youtubeService = youtubeService;
		this.youtubeUtils = youtubeUtils;
		this.videoService = videoService;
		this.reviewService = reviewService;
	}

	/** create **/
	@PostMapping("/upload")
	public ResponseEntity<String> uploadVideo(@RequestHeader("access") String token, @RequestBody Video video)
			throws IOException {
		try {
			// 유튜브 영상을 올린다?
			// 영상 링크 속 youtube video id를 추출
			String youtubeId = youtubeUtils.extractVideoId(video.getVideoLink());
			String title = youtubeService.searchByYoutubeId(youtubeId);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName();
			int userKey = userService.selectByUserId(username).getUserKey();
			video.setVideoTitle(title);
			video.setVideoChannelName(username);
			video.setReviewList(null);
			video.setUserKey(userKey);
			video.setVideoViewCnt(0);
			video.setVideoType('Y');
			// DTO를 모두 채운다.
			// youtubeService를 통해 DB에 저장한다.
			
			
			youtubeService.registVideo(video);
			return new ResponseEntity<String>(HttpStatusCode.valueOf(200));
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatusCode.valueOf(403));
		}
	}
	
	/**		update	 **/
	@PutMapping("/update/{videoKey}")
	public ResponseEntity<String> updateVideo(@PathVariable int videoKey, @RequestHeader("access") String token,
			@RequestBody Video newVideo) throws IOException {
		// 로그인한 사람이 게시한 비디오인지 먼저 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Video oldVideo = videoService.selectOne(videoKey);
		if (username.equals(oldVideo.getVideoChannelName())) {
			// 프론트에서 input 태그가 비어있는지 아닌지 체크해서 빈 태그의 경우 선택된 객체의 값을 다시 넣어 전달하도록 구성 +
			// youtube영상은 제목 수정 불가
			// 받아온 객체와 video id로 DB에서 가져온 객체 비교
			// 링크가 다르다?
			if (!oldVideo.getVideoLink().equals(newVideo.getVideoLink())) {
				String youtubeId = youtubeUtils.extractVideoId(newVideo.getVideoLink());
				String title = youtubeService.searchByYoutubeId(youtubeId);
				// 링크가 달라지면 -> 제목, 조회수, 댓글 전부 새로 설정 필요 + 나머지는 이전 객체의 값 그대로 프론트에서 유지
				newVideo.setVideoKey(videoKey);
				newVideo.setVideoTitle(title);
				newVideo.setVideoViewCnt(0);
				reviewService.deleteAll(videoKey);
				newVideo.setReviewList(null);
			} // 링크가 다르지 않으면, newVideo 받은 내용 그대로 저장

			if (youtubeService.updateVideo(newVideo)) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("수정 되었습니다.");
			} else {
				return new ResponseEntity<String>(HttpStatusCode.valueOf(500));
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("본인의 영상만 수정할 수 있습니다.");
		}
	}
	
	/**		delete	 **/
	@DeleteMapping("/delete/byKey/{videoKey}")
	public ResponseEntity<String> deleteOne(@PathVariable("videoKey") int videoKey, @RequestHeader("access") String token){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Video video = videoService.selectOne(videoKey);
		//비디오가 null? 그런 비디오 없다.
		if(video == null) {
			return ResponseEntity.notFound().build();
		}
		//로그인한 사용자가 게시한 비디오가 맞다면?
		if(video.getVideoChannelName().equals(username)) {
			if(videoService.deleteVideo(videoKey) >= 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("삭제 되었습니다.");
			}else {
				return new ResponseEntity<String>(HttpStatusCode.valueOf(500));
			}
			
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 영상만 삭제할 수 있습니다.");
		}
	}
	
	@DeleteMapping("/delete/byChannelName/{videoChannelName}")
	public ResponseEntity<String> deleteList(@PathVariable("videoChannelName") String videoChannelName, @RequestHeader("access") String token){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		
		//로그인한 사용자가 게시한 비디오가 맞다면?
		if(username.equals(videoChannelName)) {
			if(videoService.deleteList(videoChannelName) >= 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("삭제 되었습니다.");
			}else {
				return new ResponseEntity<String>(HttpStatusCode.valueOf(500));
			}
			
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인의 영상만 삭제할 수 있습니다.");
		}
	}
	
}
