package com.power.strategy;

import org.springframework.security.core.userdetails.UserDetails;

public interface LoginStrategy {

    UserDetails realStrategy(String username);
}
