package com.power.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUser implements UserDetails {

    // 后台管理系统用户属性
    /**
     * 用户标识
     */
    private Long userId;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录类型
     */
    private String loginType;
    /**
     * 用户状态
     */
    private Integer status;
//    /**
//     * 用户所属店铺标识
//     */
//    private Long shopId;
//    private  String openId;
//
//    public String getOpenId() {
//        return openId;
//    }
//
//    public void setOpenId(String openId) {
//        this.openId = openId;
//    }
//
//    /**
//     * 用户权限
//     */
    private Set<String> perms = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return  List.of();

    }

    @Override
    public String getUsername() {
        return this.loginType + this.userId;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    /**
     * 处理用户权限记录中包含多个权限的情况
     * @param perms
     */

    public void setUser(Set<String> perms)
    {

        HashSet<String> set = new HashSet<>();
        perms.forEach(each->{

            if(each.contains(","))
            {
                String[] split = each.split(",");
                Collections.addAll(set, split);
            }
            else {
                set.add(each);
            }
        });
        this.perms=set;
    }

}