package com.example.demo.service;

import com.example.demo.model.DemoUser;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public DemoUser getUser(String username) {
        return userRepository.getByUsername(username);
    }

    public List<SimpleGrantedAuthority> getRoles(String username) {
        List<Integer> roles = userRepository.getUserType(username).stream()
                .distinct()
                .filter(role -> role == 1)
                .collect(Collectors.toList());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.size());
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(String.valueOf(role))));
        return authorities;
    }
}
