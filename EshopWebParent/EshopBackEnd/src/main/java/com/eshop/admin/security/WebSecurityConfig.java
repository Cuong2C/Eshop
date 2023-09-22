package com.eshop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	UserDetailsService userDetailsService() {
		return new EshopUserDetailsService();
	}
	
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
				.requestMatchers("/users/**","/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
				.requestMatchers("/categories/**","/brands/**","/articles/**","/menu/**").hasAnyAuthority("Admin", "Editor")
				
				.requestMatchers("/products/new", "/products/delete/**" ).hasAnyAuthority("Admin","Editor")
				.requestMatchers("/products/edit/**", "/products/save", "/products/check_unique" ).hasAnyAuthority("Admin","Editor","Salesperson")
				.requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**").hasAnyAuthority("Admin","Editor","Salesperson","Shipper" )
				.requestMatchers("/products/**").hasAnyAuthority("Admin","Editor")
				
				.requestMatchers("/customer/**","/report/**").hasAnyAuthority("Admin","Salesperson")
				.requestMatchers("/shipping_rates/**").hasAnyAuthority("Admin","Shipper")
				.requestMatchers("/orders/**").hasAnyAuthority("Admin","Salesperson","Shipper")		
				.anyRequest().authenticated());
		http.formLogin(form ->form
					.loginPage("/login")
					.usernameParameter("email")
					.defaultSuccessUrl("/")
					.permitAll());
		http.logout(logout->logout
					.permitAll())
					
			.rememberMe(me -> me
		            .key("RememberMeKey")
		            .tokenValiditySeconds(7*24*60*60));
	
		return http.build();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring().requestMatchers("/images/**","/js/**","/webjars/**","/css/**");
	}
	
}
