package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/loadLoginName")
    public Map loadLoginName(){
        //从security中获取用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        //放入map返回
        Map map = new HashMap();
        map.put("loginName", loginName);
        return map;
    }
}
