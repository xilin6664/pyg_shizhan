package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * springsecurity实现自定义登录的用户查询服务接口实现类
 */
public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    //根据登录名称查询登录用户信息并返回给security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.根据username查询数据库获取用户信息
        TbSeller seller = sellerService.findOne(username);
        if(null != seller){
            //2.查询到，判断用户是否审核通过
            if("1".equals(seller.getStatus())){
                //2.1 通过，将用户信息返回给security，由security判断密码是否正确
                //参数1：登录用户名，参数二就是数据库中的密码，参数三：用户的权限关键字集合
                List<GrantedAuthority> list = new ArrayList<>();
                list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                return new User(username, seller.getPassword(), list);
            } else {
                //2.2不通过，直接返回null
                return null;
            }
        } else {
            //3.未查询到，直接返回null
            return null;
        }
    }

    public static void main(String[] args){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String result = encoder.encode("123456");
        System.out.println(result);
    }
}
