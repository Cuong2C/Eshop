package com.eshop.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eshop.admin.user.UserRepository;
import com.eshop.common.entity.User;

public class EshopUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserbyEmail(email);
		if(user != null) {
			return new EshopUserDetails(user);
		}
		throw new UsernameNotFoundException("Could not find user with email: "+ email);
	}

}