package com.power.factory;

import com.power.strategy.LoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginStrategyFactory {
    @Autowired
    private Map<String, LoginStrategy> loginStrategyMap=new HashMap<>();//存放所有策略

    public LoginStrategy getLoginStrategy(String loginType){
        return loginStrategyMap.get(loginType);
    }
}
