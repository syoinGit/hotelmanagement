package com.portfolio.hotel.management.config;

import com.portfolio.hotel.management.service.HotelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder; // 開発用
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final HotelService service;

  /** /static, /public などの静的リソースをセキュリティ対象外に */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().requestMatchers(
        PathRequest.toStaticResources().atCommonLocations()
    );
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // 未認証は 302 へ飛ばさず 401(JSON) を返す
    AuthenticationEntryPoint rest401 = (req, res, ex) -> {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      res.setContentType("application/json;charset=UTF-8");
      res.getWriter().write("{\"error\":\"Unauthorized\"}");
    };

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .exceptionHandling(e -> e.authenticationEntryPoint(rest401))
        .authorizeHttpRequests(auth -> auth
            // トップや SPA の静的配信
            .requestMatchers(HttpMethod.GET,
                "/", "/index.html",
                "/favicon.ico", "/manifest.json", "/asset-manifest.json",
                "/logo192.png", "/logo512.png",
                "/static/**", "/assets/**",
                "/error" // エラーページも開放
            ).permitAll()

            // 認証不要 API
            .requestMatchers(HttpMethod.PUT, "/user/register").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/actuator/health").permitAll()

            // それ以外は認証必須
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginProcessingUrl("/login")
            .usernameParameter("id")
            .passwordParameter("password")
            .successHandler((request, response, authentication) -> {
              response.setStatus(HttpServletResponse.SC_OK);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"message\":\"Login successful\"}");
            })
            .failureHandler((request, response, exception) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"error\":\"Login failed\"}");
            })
        )
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .userDetailsService(service);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // 開発中のみ（本番は BCrypt 等へ）
    return NoOpPasswordEncoder.getInstance();
  }

  /** CORS（開発用） */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "http://hotelmanagement.syoingit.com",
                "https://hotelmanagement.syoingit.com"
            )
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
            .allowCredentials(true);
      }
    };
  }
}