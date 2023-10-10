package com.eshop.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.admin.security.EshopUserDetails;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String viewHomePage(@AuthenticationPrincipal EshopUserDetails loggedUser, Model model) {
		boolean isAdmin = false;
		boolean isEditor = false;
		boolean isSalesperson = false;
		boolean isShipper = false;
		boolean isAssistant = false;
		
		if (loggedUser.hasRole("Admin")) {
			isAdmin = true;
		}
		
		if (loggedUser.hasRole("Assistant")) {
			isAssistant = true;
		}
		if (loggedUser.hasRole("isEditor")) {
			isEditor = true;
		}
		
		if (loggedUser.hasRole("Shipper")) {
			isShipper = true;
		}
		
		if (loggedUser.hasRole("Salesperson")) {
			isSalesperson = true;
		}
		
		model.addAttribute("isAdmin", isAdmin);
		model.addAttribute("isAssistant", isAssistant);
		model.addAttribute("isEditor", isEditor);
		model.addAttribute("isShipper", isShipper);
		model.addAttribute("isSalesperson", isSalesperson);
		
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) return "login";
		return "redirect:/";
	}
	

}
