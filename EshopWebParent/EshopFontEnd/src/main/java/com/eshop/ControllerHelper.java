package com.eshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ControllerHelper {

	@Autowired
	private CustomerService customerService;
	
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email); 
	}
}
