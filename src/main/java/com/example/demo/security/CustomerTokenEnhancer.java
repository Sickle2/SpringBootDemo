package com.example.demo.security;

import com.example.demo.model.DemoUser;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/

@Slf4j
@Component
public class CustomerTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DemoUser user = userService.getUser(authentication.getName());
        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("userId", user.getId());
        additionalInfo.put("userType", user.getUserType());
        additionalInfo.put("userName", user.getUsername());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

}
