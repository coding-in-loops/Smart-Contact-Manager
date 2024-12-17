package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.scm.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

	@Autowired
	SecurityCustomUserDetailService userDetailService;
	
	@Autowired
	OAuthAuthenticationSuccessHandler handler;
	
	@Autowired
	AuthenticationFailureHandler failureHandler;
	
	//user create and login using java code with in memory service
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails user1 = User
//				.withDefaultPasswordEncoder()
//			    .username("admin123")
//				.password("admin123")
//				.roles("ADMIN","USER")
//				.build();
//		UserDetails user2 = User
//				.withDefaultPasswordEncoder()
//				.username("user123")
//				.password("user123")
//				//.roles("ADMIN","USER")
//				.build();
//		var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1,user2);
//		return inMemoryUserDetailsManager;
//	}
	
	
	//configuration of authentication provider for spring security
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		//configure urls which one is public accessible and which one are not
		httpSecurity.authorizeHttpRequests(authorize ->{
			//authorize.requestMatchers("/home","/register","/services").permitAll();
			authorize.requestMatchers("/user/**").authenticated();
			authorize.anyRequest().permitAll();
		});
		
		//form default login
		httpSecurity.formLogin(formLogin->{
			formLogin.loginPage("/login")
			.loginProcessingUrl("/authenticate")
			.defaultSuccessUrl("/user/profile",true)
			//.failureForwardUrl("/login?error=true")
			.usernameParameter("email")
			.passwordParameter("password");
			formLogin.failureHandler(failureHandler);
		});
		
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		
		httpSecurity.logout(logoutForm->{
			logoutForm.logoutUrl("/do-logout")
			.logoutSuccessUrl("/login?logout=true");
		});
		
		//oauth configurations
		httpSecurity.oauth2Login(oauth->{
			oauth.loginPage("/login")
			.successHandler(handler);
		});
		return httpSecurity.build();
	}
}
