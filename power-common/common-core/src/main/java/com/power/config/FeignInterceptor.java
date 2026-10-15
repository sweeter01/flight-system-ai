package com.power.config;


import cn.hutool.core.util.ObjectUtil;
import com.power.constant.AuthConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate requestTemplate) {

         ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
         if(ObjectUtil.isNotNull(requestAttributes))
         {
             HttpServletRequest request = requestAttributes.getRequest();

             if (request.getHeader(AuthConstants.AUTHORIZATION) != null) {
              requestTemplate.header(AuthConstants.AUTHORIZATION, request.getHeader(AuthConstants.AUTHORIZATION));
              return;
             }
         }

    }
}