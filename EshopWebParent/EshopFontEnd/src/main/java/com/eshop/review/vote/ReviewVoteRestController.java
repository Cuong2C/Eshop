package com.eshop.review.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eshop.ControllerHelper;
import com.eshop.common.entity.Customer;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ReviewVoteRestController {

	@Autowired
	private ReviewVoteService service;
	@Autowired
	private ControllerHelper helper;
	
	@PostMapping("/vote_review/{id}/up")
	public VoteResult voteReview(@PathVariable(name = "id") Integer reviewId, HttpServletRequest request) {
		
		Customer customer = helper.getAuthenticatedCustomer(request);
		
		if (customer == null) {
			return VoteResult.fail("You must login to vote the review.");
		}
		return service.doVote(reviewId, customer);
	}
}
