package com.eshop.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.AmazonS3Util;
import com.eshop.admin.user.UserNotFoundException;
import com.eshop.common.entity.Category;
import com.eshop.common.exception.CategoryNotFoundException;

import jakarta.servlet.http.HttpServletResponse;


@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(Model model ) {
		return listByPage(1, "asc", null, model);
	}
	
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, String sortDir, String keyword, Model model ) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
		
		long startCount = (pageNum-1)*CategoryService.ROOT_CATEGORIES_PER_PAGE +1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE -1;
		if(endCount>pageInfo.getTotalElements()) endCount = pageInfo.getTotalElements();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages",pageInfo.getTotalPages());
		model.addAttribute("totalItems",pageInfo.getTotalElements());
		model.addAttribute("sortField","name");
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("keyword",keyword);
		
		
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("reverseSortDir",reverseSortDir);
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories =	service.listCategoryUsedInForm();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create a new category");
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileImg") MultipartFile multipartFile, RedirectAttributes redirectAttributes ) throws IOException {
		
		if(!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);
		
		Category savedCategory = service.save(category);		
		String uploadDir = "category-images/" + savedCategory.getId();
		
		AmazonS3Util.removeFolder(uploadDir);
		AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		}else {
			service.save(category);	
		}
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editUser(@PathVariable(name="id") Integer id, RedirectAttributes redirect, Model model ) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoryUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category(ID:"+id+")");
			model.addAttribute("listCategories",listCategories);
			return "categories/category_form";
		} catch (UserNotFoundException e) {
			
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateEnabled(@PathVariable("id") Integer id, @PathVariable("status") boolean status,RedirectAttributes redirect) {
		service.updateEnabledCategories(id, status);
		String cateStatus = status ? "enabled" : "disabled";
		redirect.addFlashAttribute("message", "The category id "+id+ " has been " +cateStatus);
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategories(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			service.delete(id);
			String categoryDir = "categories-images/" +id;
			AmazonS3Util.removeFolder(categoryDir);
			
			redirect.addFlashAttribute("message", "The category id "+id+" has been deleted successfully");		
		}catch(CategoryNotFoundException e){
			redirect.addFlashAttribute("message", e.getMessage());		
		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportCSV(HttpServletResponse reponse) throws IOException {
		List<Category> listCategories = service.listCategoryUsedInForm();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, reponse);
	}
	
}
