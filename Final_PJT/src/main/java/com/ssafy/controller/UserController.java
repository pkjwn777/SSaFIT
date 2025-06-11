package com.ssafy.controller;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.http.HttpStatusCodes;
import com.ssafy.exception.RegistException;
import com.ssafy.model.dto.JoinDTO;
import com.ssafy.model.dto.User;
import com.ssafy.model.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	
	// 자체 회원 가입
	@PostMapping("/regist")
	public ResponseEntity<String> regist(@RequestBody JoinDTO joinDTO) {
		try {
			userService.regist(joinDTO);
			return new ResponseEntity<String>("good", HttpStatusCode.valueOf(200));
		} catch (RegistException e) {
			// 회원가입 실패 페이지 필요
			return new ResponseEntity<String>("failed", HttpStatusCode.valueOf(500));
		}
	}




	// 단일 조회
	@GetMapping("/searchUser/{userKey}")
	public ResponseEntity<?> searchUser(@RequestHeader("access") String token, @PathVariable("userKey") int userKey) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();
		try {
			User user = userService.selectOne(userKey);

			if (user == null) {
				return new ResponseEntity<String>("Bad request", HttpStatusCode.valueOf(500));
			}

			boolean isSelf = userId.equals(user.getUserId());

			Map<String, Object> response = new HashMap<>();
			response.put("isSelf", isSelf);
			response.put("user", user);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return new ResponseEntity<String>("server side bad response", HttpStatusCode.valueOf(500));
		}
	}
	
	// 다수 조회
		@GetMapping("/searchList/{username}")
		public ResponseEntity<?> searchUser(@PathVariable("username") String username) {
			try {
					List<User> userList = userService.searchList(username);

				if (userList == null) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
				}

				return ResponseEntity.ok(userList);
			} catch (Exception e) {
				return new ResponseEntity<String>("server side bad response", HttpStatusCode.valueOf(500));
			}
		}

	// 팔로우 기능
	@GetMapping("{followerKey}/follow/{followingKey}")
	public ResponseEntity<?> follow(@PathVariable("followerKey") int followerKey,
			@PathVariable("followingKey") int followingKey) {
		/** axios 쓸거면 이렇게 */
		Map<String, Object> request = new HashMap<>();
		request.put("followerKey", followerKey);
		request.put("followingKey", followingKey);
		try {
			userService.follow(request);
			return ResponseEntity.ok("팔로우 되었습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>("팔로우 실패", HttpStatusCode.valueOf(500));
		}
	}

	// 다수 조회(팔로워)
	@GetMapping("/follower")
	public ResponseEntity<?> searchFollowing() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userId = authentication.getName();

			int userKey = userService.getUserKeyByUserId(userId);
			List<Integer> KeyList = userService.searchFollowing(userKey);
			List<User> userList = new ArrayList<>();
			for (int key : KeyList) {
				userList.add(userService.selectOne(key));
			}

			return ResponseEntity.ok(userList);
		} catch (Exception e) {
			return new ResponseEntity<String>("server side bad response", HttpStatusCode.valueOf(500));
		}
	}

	// 다수 조회(팔로잉)
	@GetMapping("/following")
	public ResponseEntity<?> searchFollower() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String userId = authentication.getName();

			int userKey = userService.getUserKeyByUserId(userId);
			List<Integer> KeyList = userService.searchFollower(userKey);
			List<User> userList = new ArrayList<>();
			for (int key : KeyList) {
				userList.add(userService.selectOne(key));
			}

			return ResponseEntity.ok(userList);
		} catch (Exception e) {
			return new ResponseEntity<String>("server side bad response", HttpStatusCode.valueOf(500));
		}
	}

	// 유저 수정
	@PutMapping("/update/{userKey}")
	public ResponseEntity<?> update(@PathVariable int userKey, @RequestBody User newUser) {
		User oldUser = userService.selectOne(userKey);
		if (oldUser != null) {
			newUser.setRole("User");
			newUser.setUserId(oldUser.getUserId());
			newUser.setUserKey(userKey);

			if (userService.update(newUser) >= 1) {
				return ResponseEntity.ok("수정 완료");
			}else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
		}
	}


	// 유저 삭제(탈퇴)
	@DeleteMapping("/delete/{userKey}")
	public ResponseEntity<?> delete(@PathVariable int userKey) {
		if(userService.delete(userKey))
		{
			return ResponseEntity.ok("삭제 완료");
		}
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
		}
	}
	
//	// 타 회원 운동 기록 조회(한달)
//	@PostMapping("/otherLog")
//	public String monthlyLog(@ModelAttribute User user, HttpSession session) {
//		return "redirect:/user/myPage";
//	}
//
//	// 타 회원 운동 기록 조회(한달)
//	@PostMapping("/otherdailyLog")
//	public String otherdailyLog(@ModelAttribute User user, HttpSession session) {
//		return "redirect:/user/myPage";
//	}
//
//	// 운동 로그 생성
//	@PostMapping("/createLog")
//	public String createLog(@ModelAttribute User user, HttpSession session) {
//		return "redirect:/user/myPage";
//	}
//
//	// 한달 로그 조회
//	@PostMapping("/myLog")
//	public String myLog(@ModelAttribute User user, HttpSession session) {
//		return "redirect:/user/myPage";
//	}
//
//	// 하루 로그 조회
//	@PostMapping("/mydailyLog")
//	public String mydailyLog(@ModelAttribute User user, HttpSession session) {
//		return "redirect:/user/myPage";
//	}

}
