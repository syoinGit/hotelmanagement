package com.portfolio.hotel.management.data.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class LoginUserDetails implements UserDetails {
  private final String id;
  private final String password;

  public LoginUserDetails(String id, String password) {
    this.id = id;
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList(); // 権限なし
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return id;
  }

  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}