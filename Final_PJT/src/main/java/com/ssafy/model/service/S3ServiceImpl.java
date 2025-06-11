package com.ssafy.model.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.model.dao.S3Dao;
import com.ssafy.model.dto.Video;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ServiceImpl implements S3Service {

	// s3 스토리지서 퍼블릭 권한으로 다 풀었고, ACL도 활성화 함
	// 이제 업로드 된 영상 목록에서 퍼블릭으로 돌려야 함
	// 비디오 테이블도 있어야 하나?
	// 유저 DTO 받아두고 이름이나 등등넣기
	
	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	private final S3Client s3Client;
	private final S3Dao s3Dao;
	
	public S3ServiceImpl(S3Client s3Client, S3Dao s3Dao)
	{
		this.s3Client = s3Client;
		this.s3Dao = s3Dao;
	}

	public String generateThumbnail(String s3VideoKey) {
		String fileName = s3VideoKey.substring(s3VideoKey.lastIndexOf("/") + 1); // 정말 영상 이름
	    String baseName = fileName.replace(".mp4", ""); // .mp4를 뗀 이름
	    String localVideoPath = "C:/temp/" + fileName; // local 저장하기 (영상)
	    String localThumbnailPath = "C:/temp/" + baseName + ".jpg"; // local 저장하기 (섬네일)
	    String thumbnailKey = "thumbnail/" + baseName + ".jpg";

		String ffmpegPath = "ffmpeg/ffmpeg.exe"; // 상대 경로로 실행
	
		try {
	        // 1. S3에서 영상 다운로드
	        GetObjectRequest getRequest = GetObjectRequest.builder()
	                .bucket(bucketName)
	                .key(s3VideoKey)
	                .build();
	        s3Client.getObject(getRequest, Paths.get(localVideoPath));

	        // 2. ffmpeg로 썸네일 추출
	        ProcessBuilder pb = new ProcessBuilder(
	            ffmpegPath, "-i", localVideoPath, "-ss", "00:00:03", "-vframes", "1", localThumbnailPath);
	        
	        pb.redirectErrorStream(true);
	        Process process = pb.start();
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
	        while (reader.readLine() != null); // 로그 무시
	        int exitCode = process.waitFor();
	        if (exitCode != 0) {
	            throw new RuntimeException("썸네일 생성 실패: ffmpeg exit code " + exitCode);
	        }

	        // 3. 썸네일 S3에 업로드
	        PutObjectRequest putRequest = PutObjectRequest.builder()
	                .bucket(bucketName)
	                .key(thumbnailKey)
	                .acl("public-read")
	                .build();
	        s3Client.putObject(putRequest, Paths.get(localThumbnailPath));

	        return "https://" + bucketName + ".s3.amazonaws.com/" + thumbnailKey;

	    } catch (Exception e) {
	        throw new RuntimeException("썸네일 생성 중 오류: " + e.getMessage(), e);
	    } finally {
	        new File(localVideoPath).delete();
	        new File(localThumbnailPath).delete();
	    }
	}
	
	@Override
	public boolean registVideo(Video video, MultipartFile multipartFile) {
		try {
			// DB 규칙(제목) : 사용자 정의 비디오 이름_파일명.mp4
            video.setVideoTitle(video.getVideoTitle() + "_" + multipartFile.getOriginalFilename());
            video.setVideoLink("nothing");
            s3Dao.insertVideo(video); /** 이거 exception 던질때 구현 필요 */
            
            // S3 스토리지 규칙(제목) : 비디오 키_업로더 이름_사용자 정의 비디오 이름_파일명.mp4
            String fileName = video.getVideoKey() + "_" + video.getVideoChannelName() + "_" + video.getVideoTitle(); 
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .acl("public-read")  // 퍼블릭 읽기 권한
                    .contentDisposition("inline") // 브라우저에서 바로 재생 가능
                    .contentType("video/mp4")
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));
            
            video.setVideoLink("https://ssafy-ssafit.s3.us-east-1.amazonaws.com/" + fileName);
            s3Dao.setDatabaseLink(video);
            
            
    	    String videoUrl = video.getVideoLink();

    	    String s3Key = videoUrl.substring(videoUrl.indexOf(".com/") + 5); // ".com/" 다음부터 key
    	    generateThumbnail(s3Key);

            return true;
        } catch (Exception e) {
        	e.printStackTrace();
           	return false;
        }
    }
    
    public List<Video> selectByTitle(String title)
    {
        return s3Dao.selectByTitle(title);			
    }

	@Override
	public Video selectByKey(int videoKey) {
		return s3Dao.selectByKey(videoKey);
	}

	@Override
	public List<Video> selectByFitPartName(String fitPartName) {
		return s3Dao.selectByFitPartName(fitPartName);
	}

	@Override
	public List<Video> selectByChannelName(String channelName) {
		return s3Dao.selectByChannelName(channelName);
	}
	
	@Override
	public void copyVideo(String sourceKey, String destinationKey) {
	    HeadObjectResponse head = s3Client.headObject(HeadObjectRequest.builder()
	            .bucket(bucketName)
	            .key(sourceKey)
	            .build());

	    Map<String, String> metadata = head.metadata();

	    CopyObjectRequest copyReq = CopyObjectRequest.builder()
	            .sourceBucket(bucketName)
	            .sourceKey(sourceKey)
	            .destinationBucket(bucketName)
	            .destinationKey(destinationKey)
	            .metadataDirective(MetadataDirective.REPLACE)
	            .contentType(head.contentType())
	            .contentDisposition("inline")
	            .acl(ObjectCannedACL.PUBLIC_READ)
	            .metadata(metadata)
	            .build();

	    s3Client.copyObject(copyReq);
	}

	@Override
	public boolean updateVideo(Video video) {
		return s3Dao.updateVideo(video) == 1;
	}
	
	@Override
	public boolean deleteVideo(int videoKey, String title) {
		try {
			s3Client.deleteObject(
	                DeleteObjectRequest.builder()
	                        .bucket(bucketName)
	                        .key(title)
	                        .build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// db 날려주고
		s3Dao.deleteVideo(videoKey);
		return true;
	}
	
	@Override
	public void deleteFileOnlyFromS3(String title) {
	    s3Client.deleteObject(DeleteObjectRequest.builder()
	            .bucket(bucketName)
	            .key(title)
	            .build());
	}
}
