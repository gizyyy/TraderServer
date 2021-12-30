package com.example.business;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraderConfig {

	@Bean
	public ConcurrentHashMap<Integer, List<Integer>> tradeMap(){
		return new ConcurrentHashMap<>();
	}
}