package com.eshop.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.eshop.admin.product.ProductService;
import com.eshop.common.entity.product.Product;

@Controller
public class ProductSearchController {

	@Autowired
	private ProductService service;
	
	@GetMapping("/orders/search_product")
	public String showSearchProductPage(Model model,String  keyword) {
		return searchProductsByPage(1, model ,"name", "asc", keyword);
	}
	
	@PostMapping("/orders/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
	}
	
	@GetMapping("/orders/search_product/page/{pageNum}")
	public String searchProductsByPage(@PathVariable(name="pageNum") int pageNum, Model model, String sortField, String sortDir, String keyword) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
		Page<Product> page = service.searchProducts(pageNum, sortField, sortDir, keyword);
	
		List<Product> listProducts = page.getContent();

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*ProductService.PRODUCTS_PER_PAGE +1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
	
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);

		return "orders/search_product";
	}
}
