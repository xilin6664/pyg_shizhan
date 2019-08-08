package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/index")
public class IndexController {

	@RequestMapping("/findLoginUser")
	public Map<String,String> findLoginUser(){
		//1.从springsecuirty中获取用户信息
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		//2.将用户用username为key封装到map中，返回
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		return map;
	}
}
