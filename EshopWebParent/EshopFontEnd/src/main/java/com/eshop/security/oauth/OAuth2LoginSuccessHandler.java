package com.eshop.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.eshop.common.entity.AuthenticationType;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerOAuth2Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Autowired 
	private CustomerOAuth2Service customerOauthService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User oauth2User = (CustomerOAuth2User) authentication.getPrincipal();
		
		String name = oauth2User.getName();
		String email = oauth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName = oauth2User.getClientName();
		
		AuthenticationType authenticationType = getAuthenticationType(clientName);
		
		Customer customer = customerOauthService.getCustomerByEmail(email);
		if (customer == null) {
			customerOauthService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
		} else {
			oauth2User.setFullName(customer.getFullName());
			customerOauthService.updateAuthenticationType(customer, authenticationType);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	private AuthenticationType getAuthenticationType(String clientName) {
		if (clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		} else if (clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		} else {
			return AuthenticationType.DATABASE;
		}
	}
	
}
