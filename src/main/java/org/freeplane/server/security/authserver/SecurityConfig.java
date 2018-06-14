package org.freeplane.server.security.authserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
		    .antMatchers("/login", "/oauth/authorize")
		    .and()
		    .authorizeRequests()
		    .anyRequest().authenticated()
		    .and()
		    .formLogin().permitAll();
	}
	 
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.parentAuthenticationManager(authenticationManager)
		    .inMemoryAuthentication()
		    // TODO: This needs to be updated with a user/pwd that freeplane-server will use
		    .withUser("dimitry").password("yrtimid").roles("USER");
	}
}
