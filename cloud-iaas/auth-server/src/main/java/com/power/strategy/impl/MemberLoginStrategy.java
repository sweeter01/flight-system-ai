//package com.power.strategy.impl;
//
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.power.constant.AuthConstants;
//import com.power.domain.LoginMember;
//import com.power.mapper.LoginMemberMapper;
//import com.power.model.SecurityUser;
//import com.power.strategy.LoginStrategy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.InternalAuthenticationServiceException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.sql.Date;
//
//@Service(AuthConstants.MEMBER_LOGIN)
//public class MemberLoginStrategy implements LoginStrategy {
//
//    @Autowired
//    private WxParamConfig wxParamConfig;
//
//    @Autowired
//    private LoginMemberMapper loginMemberMapper;
//    @Override
//    public UserDetails realStrategy(String username) {
//        // 调用微信接口服务器中的：登录凭证校验接口（appid,appsecret,code）
////        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
//        // 获取登录凭证校验接口url
//        String realUrl = String.format(wxParamConfig.getUrl(), wxParamConfig.getAppid(), wxParamConfig.getSecret(), username);
//        // 使用get方法调用登录凭证校验接口
//        String jsonStr = HttpUtil.get(realUrl);
//        // 判断响应是否有值
//        if (!StringUtils.hasText(jsonStr)) {
//            throw new InternalAuthenticationServiceException("登录异常，请重试");
//        }
//        //将登录凭证校验接口响应的json格式的字符串转换为json对象
//        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//        String openid = jsonObject.getString("openid");
//        // 判断是否有值
//        if (!StringUtils.hasText(openid)) {
//            throw new InternalAuthenticationServiceException("登录异常，请重试");
//        }
//        //根据openId获取会员信息
//        LoginMember loginMember = loginMemberMapper.selectOne(new LambdaQueryWrapper<LoginMember>()
//                .eq(LoginMember::getOpenId, openid));
//
//        if (loginMember == null)
//        {
//            loginMember = createLoginUser(openid);
//        }
//        // 判断会员帐号的状态
//        if (!loginMember.getStatus().equals(1)) {
//            throw new InternalAuthenticationServiceException("帐号异常，请联系平台工作人员");
//        }
//        // 会员存在：返回security框架能够认识的安全用户对象SecurityUser
//        SecurityUser securityUser = new SecurityUser();
//        securityUser.setUserId(Long.valueOf(loginMember.getId()));
//        securityUser.setLoginType(AuthConstants.MEMBER_LOGIN);
//        securityUser.setUsername(openid);
//        securityUser.setStatus(loginMember.getStatus());
//        securityUser.setPassword(wxParamConfig.getPwd());
//        securityUser.setOpenId(openid);
//
//        return securityUser;
//    }
//
//        private LoginMember createLoginUser(String openid) {
//            ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = requestAttributes.getRequest();
//            String ip = request.getRemoteAddr();
//            long time = System.currentTimeMillis();
//            LoginMember loginMember = new LoginMember();
//            loginMember.setOpenId(openid);
//            loginMember.setStatus(1);
//            loginMember.setCreateTime(new Date(time));
//            loginMember.setUpdateTime(new Date(time));
//            loginMember.setUserLasttime(new Date(time));
//            loginMember.setUserRegip(ip);
//            loginMember.setUserLastip(ip);
//            loginMemberMapper.insert(loginMember);
//            return loginMember;
//
//    }
//}
