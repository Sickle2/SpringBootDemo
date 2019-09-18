package com.example.demo.config;

import com.example.demo.model.DemoUser;
import com.example.demo.security.SecurityAuthenticationProvider;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;
    @Autowired
    private SecurityAuthenticationProvider provider;

    /**
     * @param auth
     * @Description:
     * @return: void
     * @author: sickle
     * @Date: 下午6:32 2019/9/17
     */
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
//        auth.jdbcAuthentication();
        auth.authenticationProvider(provider);
        auth.userDetailsService(username -> {
            DemoUser user = userService.getUser(username);
            if (user == null) {
                throw new OAuth2Exception("没有此用户!");
            }
            List<SimpleGrantedAuthority> authorities = userService.getRoles(username);
            return User.withUsername(username).password(user.getPassword()).authorities(authorities).build();
        })
                //密码加密有多重形式
                .passwordEncoder(new PasswordEncoder() {
                    @Override
                    public String encode(CharSequence charSequence) {
                        return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
                    }

                    @Override
                    public boolean matches(CharSequence charSequence, String s) {
                        return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes()).equals(s);
                    }
                });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**")
                .permitAll()
                .and()
                //退出处理
//                .logout()
//                .logoutSuccessHandler(new ELogoutSuccessHandler())
//                .and()
                .csrf().disable();

        //开启自动配置的登陆功能，效果，如果没有登陆，没有权限就会来到登陆页面
//        http.formLogin().usernameParameter("user").passwordParameter("pwd").loginPage("/userlogin");
        //1、 /login 来到登陆页
        //2、重定向到/login?error表示登陆失败
        //3、更多详细功能
        //4、默认post形式的 /login 代表处理登陆
        //5、一旦定制loginPage  那么 loginPage的post请求就是登陆


        //开启记住我功能
//        http.rememberMe().rememberMeParameter("remeber");
        //登陆成功以后，将cookie发给浏览器保存，以后访问页面带上这个cookie，只要通过检查就可以免登陆
        //点击注销会删除cookie
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
