package com.pinyougou.user.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * springsecurity实现自定义登录的用户查询服务接口实现类
 */
public class UserDetailServiceImpl implements UserDetailsService {

    //根据登录名称查询登录用户信息并返回给security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //2.1 通过，将用户信息返回给security，由security判断密码是否正确
        //参数1：登录用户名，参数二就是数据库中的密码，参数三：用户的权限关键字集合
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new User(username, "", list);
    }
}
