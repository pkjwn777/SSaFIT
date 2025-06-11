package com.ssafy.exception;

public class VideoNotFoundException extends Exception{
	public VideoNotFoundException(){
		System.out.println("찾는 비디오가 없습니다.");
	}
}
