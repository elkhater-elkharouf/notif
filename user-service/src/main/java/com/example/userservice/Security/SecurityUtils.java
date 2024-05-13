package com.example.userservice.Security;


import com.example.userservice.Services.User.UserDetailsServiceImp;
import com.example.userservice.Services.User.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
@Component
public class SecurityUtils {
    private final UserDetailsServiceImp userDetailsService;

    public SecurityUtils(UserDetailsServiceImp userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public Integer  getCurrentUserId() {
        String email = getCurrentUserEmail();
        if (email != null) {
            return userDetailsService.getUserIdByEmail(email);
        }
        return null;
    }

public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(authentication != null){
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        } else if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
    }
    return null;
}
}
