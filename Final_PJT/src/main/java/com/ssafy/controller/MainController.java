package com.ssafy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/** Vue 만들어 보고 지울지 말지 결정 */
@RestController
public class MainController {
	@GetMapping("/")
	public void redirectToMain() {
    }

}
