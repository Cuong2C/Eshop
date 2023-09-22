package com.eshop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.eshop.security.oauth.CustomerOAuth2UserService;
import com.eshop.security.oauth.OAuth2LoginSuccessHandler;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired 
	private CustomerOAuth2UserService oAuth2UserService;
	
	@Autowired 
	private OAuth2LoginSuccessHandler oauth2LoginHandler;
	
	@Autowired 
	private DatabaseLoginSuccessHandler databaseLoginHandler;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Bean
	ProviderManager authManagerBean(AuthenticationProvider provider) {
	    return new ProviderManager(provider);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.authorizeHttpRequests(auth->auth
				.requestMatchers("/account_details","/update_account_details", "/cart", "/address_book/**").authenticated()
				.anyRequest().permitAll())
			.formLogin(form ->form
				.loginPage("/login")
				.usernameParameter("email")
				.successHandler(databaseLoginHandler)
				.permitAll())
			.oauth2Login(oauth2Login -> oauth2Login
				.loginPage("/login")
				.userInfoEndpoint((userInfo) -> userInfo
					.userService(oAuth2UserService))
				.successHandler(oauth2LoginHandler))
		
			.logout(logout->logout
				.permitAll()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logoutSuccess=true"))
				
			.rememberMe(me -> me
	            .key("RememberMeKey")
	            .tokenValiditySeconds(7*24*60*60));
				
		return http.build();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring().requestMatchers("/images/**","/js/**","/webjars/**","/css/**");
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}
	
	
}
