package org.freeplane.server.security;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableOAuth2Sso
public class UISecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
          .authorizeRequests()
          .antMatchers("/", "/login**")
          .permitAll()
          .anyRequest()
          .authenticated();
    }
}