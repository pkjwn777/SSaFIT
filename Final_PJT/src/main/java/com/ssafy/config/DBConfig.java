package com.ssafy.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.ssafy.model.dao")  // 경로도 점검!
public class DBConfig {
	
}