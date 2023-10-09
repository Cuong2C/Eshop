package com.eshop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eshop.brand.BrandService;
import com.eshop.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.product.Product;
import com.eshop.product.ProductService;

@Controller
public class MainController {
	
	@Autowired 
	private CategoryService categoryService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);
		
		List<Brand> listBrands =  brandService.listAll();
		model.addAttribute("listBrands", listBrands);
		
		List<Product> list8ProductMostDelivered = productService.list8MostSalesProduct();
		model.addAttribute("list8ProductMostDelivered", list8ProductMostDelivered);
		
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}	

}
