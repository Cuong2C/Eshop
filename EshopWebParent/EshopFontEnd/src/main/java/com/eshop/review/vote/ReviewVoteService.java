package com.eshop.review.vote;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.ReviewVote;
import com.eshop.review.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewVoteService {

	@Autowired 
	private ReviewRepository reviewRepo;
	@Autowired 
	private ReviewVoteRepository voteRepo;
	
	public VoteResult undoVote(ReviewVote vote, Integer reviewId) {
		voteRepo.delete(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success("You have unvoted that review.", voteCount);
	}
	
	public VoteResult doVote(Integer reviewId, Customer customer) {
		Review review = null;
		
		try {
			review = reviewRepo.findById(reviewId).get();
		} catch (NoSuchElementException e) {
			return VoteResult.fail("The review ID " + reviewId + " no longer exists.");
		}
		
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		
		if (vote != null) {
			return undoVote(vote, reviewId);
		} else {
			vote = new ReviewVote();
			vote.setCustomer(customer);
			vote.setReview(review);	
			vote.voteUp();

		}	
		voteRepo.save(vote);
		reviewRepo.updateVoteCount(reviewId);
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		
		return VoteResult.success("You have successfully voted that review.", voteCount);
	}
	
	public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId,
			Integer customerId) {
		List<ReviewVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		for (ReviewVote vote : listVotes) {
			Review votedReview = vote.getReview();
			
			if (listReviews.contains(votedReview)) {
				int index = listReviews.indexOf(votedReview);
				Review review = listReviews.get(index);
				
				if (vote.isUpvoted()) {
					review.setUpvotedByCurrentCustomer(true);
				}
			}
		}
	}
}
