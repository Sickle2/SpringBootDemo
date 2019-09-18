package com.example.demo.config;

import com.example.demo.model.DemoUser;
import com.example.demo.security.CustomerTokenEnhancer;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Arrays;
import java.util.List;

/**
 * @program: demo
 * @description: oauth config
 * @author: sickle
 * @create: 2019-09-17 16:25
 **/
@Slf4j
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PropsConfig propsConfig;
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    private CustomerTokenEnhancer tokenEnhancer;
    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode("123456");

        clients.inMemory()
                .withClient(propsConfig.getOauthClient())
                .authorizedGrantTypes("client_credentials", "password", "refresh_token")
                .scopes("read", "write")
                .authorities(propsConfig.getOauthAuthor())
//                .secret(passwordEncoder().encode("password"))
                //自己选加密方式
                .secret(finalSecret)
                .accessTokenValiditySeconds(propsConfig.getAccessTokenExpire())
                .refreshTokenValiditySeconds(propsConfig.getRefreshTokenExpire());
        //如果有其他用户可以使用 .and().
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //token 信息存到服务内存
//        endpoints.tokenStore(new InMemoryTokenStore())
//                .authenticationManager(authenticationManager);

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customerEnhancer(), jwtAccessTokenConverter()));

        endpoints.tokenStore(redisTokenStore())
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
//        //配置TokenService参数
//        DefaultTokenServices tokenService = new DefaultTokenServices();
//        tokenService.setTokenStore(endpoints.getTokenStore());
//        tokenService.setSupportRefreshToken(true);
//        tokenService.setClientDetailsService(endpoints.getClientDetailsService());
//        tokenService.setTokenEnhancer(endpoints.getTokenEnhancer());
//        //1小时
//        tokenService.setAccessTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
//        //1小时
//        tokenService.setRefreshTokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(1));
//        tokenService.setReuseRefreshToken(false);
//        endpoints.tokenServices(tokenService);
                .userDetailsService(username -> {
                    DemoUser user = userService.getUser(username);
                    List<SimpleGrantedAuthority> authorities = userService.getRoles(username);
                    return User.withUsername(username).password(user.getPassword()).authorities(authorities).build();
                }).tokenEnhancer(tokenEnhancerChain);

    }

    @Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(connectionFactory);
    }

    @Bean
    public TokenEnhancer customerEnhancer() {
        return tokenEnhancer;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer config) throws Exception {
        super.configure(config);
        config.checkTokenAccess("permitAll()");
//        config.authenticationEntryPoint(unauthorizedEntryPoint)
//                .accessDeniedHandler(accessDeniedHandler);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("keystore/mytest.jks"), "mypass".toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        converter.setAccessTokenConverter(new DefaultAccessTokenConverter());
        return converter;
    }

    public static void main(String[] args) {
        String finalSecret = "{bcrypt}" + new BCryptPasswordEncoder().encode("123456");
        System.out.println(finalSecret);
    }
//    生成JKS Java KeyStore文件
//    keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore mytest.jks -storepass mypass
//    导出公钥
//    keytool -list -rfc --keystore mytest.jks | openssl x509 -inform pem -pubkey
}
