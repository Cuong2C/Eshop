package com.eshop.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.brand.BrandService;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.ShippingRate;

@Controller
public class ShippingRateController {
	
	@Autowired 
	private ShippingRateService service;
	
	@GetMapping("/shipping_rates")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "id", "asc", null);
	}
	
	@GetMapping("/shipping_rates/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model, String sortField, String sortDir,String keyword ) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
		Page<ShippingRate> page = service.listByPage(pageNum,sortField, sortDir, keyword );
		List<ShippingRate> listShippingRates = page.getContent();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*BrandService.BRANDS_PER_PAGE +1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
		
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listShippingRates", listShippingRates);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortField", sortField);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "shipping_rates/shipping_rates";		
	}
	
	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
		List<Country> listCountries = service.listAllCountries();
		
		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "New Rate");
		
		return "shipping_rates/shipping_rate_form";		
	}

	@PostMapping("/shipping_rates/save")
	public String saveRate(ShippingRate rate, RedirectAttributes redirect) {
		try {
			service.save(rate);
			redirect.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException e) {
			redirect.addFlashAttribute("errMessage", e.getMessage());
		}
		return "redirect:/shipping_rates";
	}
		
	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			ShippingRate rate = service.get(id);
			List<Country> listCountries = service.listAllCountries();
			
			model.addAttribute("listCountries", listCountries);			
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");
			
			return "shipping_rates/shipping_rate_form";
		} catch (ShippingRateNotFoundException ex) {
			redirect.addFlashAttribute("message", ex.getMessage());
			return "redirect:/shipping_rates";
		}
	}

	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "supported") Boolean supported,
			Model model, RedirectAttributes redirect) {
		try {
			service.updateCODSupport(id, supported);
			redirect.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException ex) {
			redirect.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			service.delete(id);
			ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/shipping_rates";
	}	
}
