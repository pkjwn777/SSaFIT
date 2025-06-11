package com.ssafy.Youtube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtils {
	public static String extractVideoId(String url) {
        String pattern = "(?:youtu\\.be/|youtube\\.com/(?:watch\\?.*?v=|embed/|v/|shorts/))([a-zA-Z0-9_-]{11})";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null; // video ID를 추출하지 못한 경우
        }
    }

}
