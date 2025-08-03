package com.portfolio.hotel.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRFはとりあえず無効化（必要ならあとで有効に）
        .csrf(csrf -> csrf.disable())

        // 認可の設定
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/user/login", "/user/register").permitAll()
            .anyRequest().authenticated()
        )

        // フォームログインの設定（独自ログイン画面を使う）
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .permitAll()
        )

        // ログアウトの設定
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

    return http.build();
  }
}