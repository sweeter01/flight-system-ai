package com.power.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {
    /**
     * token令牌
     */
    private String accessToken;

    /**
     * 过期时间（单位：秒）
     */
    private Long expireIn;
}
