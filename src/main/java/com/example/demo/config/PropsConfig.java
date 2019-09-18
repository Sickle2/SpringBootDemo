package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @program: demo
 * @description: 加载配置文件
 * @author: sickle
 * @create: 2019-09-17 16:45
 **/

@Getter
@Component
@PropertySource(value = {"classpath:config.properties"})
public class PropsConfig {

    @Value("${oauth2.client}")
    private String oauthClient;

    @Value("${oauth2.secret}")
    private String oauthSecret;

    @Value("${oauth2.author}")
    private String oauthAuthor;

    @Value("${oauth.access.second}")
    public int accessTokenExpire;

    @Value("${oauth2.refresh.second}")
    public int refreshTokenExpire;
}
