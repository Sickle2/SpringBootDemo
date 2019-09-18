package com.example.demo.security;

import com.example.demo.model.DemoUser;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Objects;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/

@Slf4j
@Component
public class SecurityAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        log.debug("account:{}", username);
        DemoUser user = userService.getUser(username);
        if (user == null) {
//            throw new CustomOauthException(Type.EmailNotFound, "UsernameNotFoundException");
            log.error("用户名不存在");
        }

//        String encodedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        log.debug("originPassword:{} encodedPassword:{} dbPassWord:{}",
                password, password, user.getPassword());
        if (!Objects.equals(password, user.getPassword())) {
//            throw new CustomOauthException(Type.BadClientCredentials, "BadCredentialsException");
            log.error("BadCredentialsException");
        }

        List<SimpleGrantedAuthority> authorities = userService.getRoles(username);
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}