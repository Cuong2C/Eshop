package com.eshop.admin.customer;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.product.ProductCsvExporter;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.product.Product;
import com.eshop.common.exception.CustomerNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomerController {

	
	@Autowired 
	private CustomerService service;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum") int pageNum, String sortField, String sortDir, String keyword) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
		Page<Customer> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomers = page.getContent();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*CustomerService.CUSTOMERS_PER_PAGE +1;
		long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalTtems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listCustomers", listCustomers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirect) {
		service.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Customer ID " + id + " has been " + status;
		redirect.addFlashAttribute("message", message);
		
		return "redirect:/customers";
	}	
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			Customer customer = service.get(id);
			model.addAttribute("customer", customer);
			
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";		
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			Customer customer = service.get(id);
			List<Country> countries = service.listAllCountries();
			
			model.addAttribute("listCountries", countries);			
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
			
			return "customers/customer_form";
			
		} catch (CustomerNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes redirect) {
		service.save(customer);
		redirect.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");
		return "redirect:/customers";
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirect) {
		try {
			service.delete(id);			
			redirect.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
			
		} catch (CustomerNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/export/csv")
	public void exportCSV(HttpServletResponse reponse) throws IOException {
		List<Customer> listProducts = service.listAll();
		CustomerCsvExporter exporter = new CustomerCsvExporter();
		exporter.export(listProducts, reponse);
	}
}
