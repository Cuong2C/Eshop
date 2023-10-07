package com.eshop.admin.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.common.entity.Review;
import com.eshop.common.exception.ReviewNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {
	
	@Autowired
	private ReviewService service;
	
	@GetMapping("/reviews")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listByPage(1, model, "id", "asc", null, request);
	}

	@GetMapping("/reviews/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model, String sortField, String sortDir,String keyword, HttpServletRequest request) {

		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
		
		Page<Review> page = service.listByPage(pageNum,sortField, sortDir, keyword );
		List<Review> listReviews = page.getContent();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*ReviewService.REVIEWS_PER_PAGE +1;
		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "reviews/reviews";
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			Review review = service.get(id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException e) {
			redirect.addFlashAttribute("errMessage", e.getMessage());
			return "redirect:/reviews";		
		}
	}
	
	@GetMapping("/reviews/edit/{id}")
	public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			Review review = service.get(id);
			
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", String.format("Edit Review (ID: %d)", id));
			
			return "reviews/review_form";
		} catch (ReviewNotFoundException e) {
			redirect.addFlashAttribute("errMessage", e.getMessage());
			return "redirect:/reviews";		
		}
	}	
	
	@PostMapping("/reviews/save")
	public String saveReview(Review reviewInForm, RedirectAttributes redirect) {
		service.save(reviewInForm);		
		redirect.addFlashAttribute("message", "The review ID " + reviewInForm.getId() + " has been updated successfully.");
		return "redirect:/reviews";		
	}
	
	@GetMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable("id") Integer id, RedirectAttributes redirect) {
		try {
			service.delete(id);
			redirect.addFlashAttribute("message", "The review ID " + id + " has been deleted.");
		} catch (ReviewNotFoundException e) {
			redirect.addFlashAttribute("errMessage", e.getMessage());
		}
		
		return "redirect:/reviews";		
	}
	
}
