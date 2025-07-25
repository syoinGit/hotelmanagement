package com.portfolio.hotel.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppConfig {

  @Bean
  public Clock systemClock() {
    return Clock.systemDefaultZone();
  }
}