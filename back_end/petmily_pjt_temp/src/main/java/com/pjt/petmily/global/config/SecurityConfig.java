package com.pjt.petmily.global.config;

import com.pjt.petmily.domain.social.OAuthService;
import com.pjt.petmily.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
//import com.pjt.petmily.domain.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final UserService userService;

    OAuthService oAuthService;


    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/**");
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성 ( 이 메서드에서 인증/인가 및 로그인, 로그아웃 관련 설정할 수 있음)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/**").permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .logout((logout) -> logout.logoutSuccessUrl("/"))
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/oauth/loginInfo", true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService)));

        return http.build();
    }

    // 7. 인증 관리자 관련 설정
    // 사용자 정보를 가져올 서비스를 재정의하거나, 인증 방법 (ex. LDAP, JDBC 기반 인증 등을 설정할 때 사용)
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService)
//            throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userService)// 8. 사용자 정보 서비스 설정 (이 때 설정하는 서비스 클래스는 반드시 UserDetailsService를 상속받은 클래스)
//                .passwordEncoder(bCryptPasswordEncoder) // 비밀번호 암호화위한 인코더
//                .and()
//                .build();
//    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
