package com.ssafy.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.model.dto.Video;
import com.ssafy.model.service.S3Service;
import com.ssafy.model.service.UserService;
import com.ssafy.model.service.VideoService;

@RestController
@RequestMapping("/s3")
public class S3Controller {
    
    private final S3Service s3Service;
    private final UserService userService;
    private final VideoService videoService;
    
    public S3Controller(S3Service s3Service, UserService userService, VideoService videoService)
    {
        this.s3Service = s3Service;
        this.userService = userService;
        this.videoService = videoService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("video") Video video,
            @RequestPart("multipartFile") MultipartFile multipartFile)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            
            video.setVideoChannelName(userId);
            int userKey = userService.getUserKeyByUserId(userId);
            video.setUserKey(userKey);
            
            video.setVideoType('S');
            video.setVideoViewCnt(0);
            
            s3Service.registVideo(video, multipartFile);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("File upload failed", HttpStatusCode.valueOf(500));
        }
        return new ResponseEntity<String>("File uploaded successfully", HttpStatusCode.valueOf(200));
    }
    
    @PutMapping("/update/{videoKey}")
    public ResponseEntity<String> updateVideo(@PathVariable int videoKey, @RequestPart("video") Video video,
            @RequestPart("multipartFile") MultipartFile multipartFile)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // S3 스토리지의 이름과 링크는 따로 수정이 안되어 복사 후 삭제를 추천
        Video updatedVideo = videoService.selectOne(videoKey);

        if (!username.equals(updatedVideo.getVideoChannelName()))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("본인의 영상만 수정할 수 있습니다.");
        }
            
        String oldS3FileName = updatedVideo.getVideoKey() + "_" +
                updatedVideo.getVideoChannelName() + "_" +
                updatedVideo.getVideoTitle();
        String newTitle = video.getVideoTitle();
        String newFitPartName = video.getVideoFitPartName();

        if (newTitle != null && !newTitle.isBlank()) {
            updatedVideo.setVideoTitle(newTitle);
        }
        
        if (newFitPartName != null && !newFitPartName.isBlank()) {
            updatedVideo.setVideoFitPartName(newFitPartName);
        }

        /** 살짝 걸리는게 비디오 키는 그대로 씀, 디버깅 할때 확인해야 할 듯 */
        String newS3FileName = updatedVideo.getVideoKey() + "_" +
                updatedVideo.getVideoChannelName() + "_" +
                updatedVideo.getVideoTitle();

        if (multipartFile == null || multipartFile.isEmpty()) {
        	String[] nameTokens = updatedVideo.getVideoTitle().split("_");
            String multipartFileName = nameTokens[1];
            
            updatedVideo.setVideoTitle(newTitle + "_" + multipartFileName);        
            
        	s3Service.copyVideo(oldS3FileName, newS3FileName);
            s3Service.deleteFileOnlyFromS3(oldS3FileName);

            // 링크 업데이트 및 Local DB에 수정사항 요청
            updatedVideo.setVideoLink("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/" + newS3FileName);
            
            s3Service.updateVideo(updatedVideo);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("제목/부위가 수정되었습니다.");
        }
        else
        {
        	// 새 영상 업로드
            uploadFile(updatedVideo, multipartFile);
            
            // 이전 영상 삭제
            s3Service.deleteVideo(videoKey, oldS3FileName);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("영상 및 정보가 수정되었습니다.");
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteVideo(@RequestParam int videoKey, @RequestParam String title,
            @RequestParam String channelName, @RequestParam char type) {
        if(type == 'S')
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            
            String s3Title = videoKey + "_" + channelName + "_" + title;
            if (channelName.equals(userId)) {
                s3Service.deleteVideo(videoKey, s3Title);
            } else {
                return new ResponseEntity<String>("Video delete failed", HttpStatus.valueOf(500));
            }
            return new ResponseEntity<String>("deleted", HttpStatus.valueOf(200));
        }
        
        return new ResponseEntity<String>("Video delete failed (type y)", HttpStatus.valueOf(500));
    }
}