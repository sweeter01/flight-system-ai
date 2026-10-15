package com.power.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.config.WhiteUrlsConfig;
import com.power.constant.AuthConstants;
import com.power.constant.BusinessEnum;
import com.power.constant.HttpConstants;
import com.power.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private WhiteUrlsConfig whiteUrlsConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 请求路径
        String path = request.getPath().toString();
        //判断请求路径是否在白名单中
        List<String> allowUrls = whiteUrlsConfig.getAllowUrls();
        if (!CollectionUtils.isEmpty(allowUrls) && allowUrls.contains(path))
        {
            //直接放行
            return chain.filter(exchange);
        }
        //不在白名单中则验证token
        String tokenVal = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION);

        if(StringUtils.hasText(tokenVal))
        {
            String realToken = tokenVal.replaceFirst(AuthConstants.BEARER, ""); // 移除Bearer前缀
            if(StringUtils.hasText(realToken) && stringRedisTemplate.hasKey(AuthConstants.LOGIN_TOKEN_PREFIX+realToken) )
            {
                return chain.filter(exchange);
            }

        }
        //验证失败
        log.error("token验证失败");
        // 拦截非法请求，响应消息
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 设置HTTP状态码为401
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 设置响应头，响应内容格式为json数据格式
        response.getHeaders().set(HttpConstants.CONTENT_TYPE,HttpConstants.APPLICATION_JSON);
        response.getHeaders().remove("WWW-Authenticate");

        Result<Object> fail = Result.fail(BusinessEnum.UN_AUTHORIZATION);

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes= new byte[0];
        try {
             bytes = objectMapper.writeValueAsBytes(fail);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(wrap));

    }

    @Override
    public int getOrder() {
        return 0;
    }
}