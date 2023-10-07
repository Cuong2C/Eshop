package com.eshop.admin.review;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.admin.product.ProductRepository;
import com.eshop.common.entity.Review;
import com.eshop.common.exception.ReviewNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	
	public static final int REVIEWS_PER_PAGE = 10;
	
	@Autowired
	private ReviewRepository reviewRepo;
	@Autowired
	private ProductRepository productRepo;
	
	public List<Review> listAll(){
		return (List<Review>)reviewRepo.findAll(Sort.by("id").ascending());
	}
	
	public Page<Review> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1, REVIEWS_PER_PAGE, sort);
		if(keyword != null) return reviewRepo.findAll(keyword, pageable);
		
		return reviewRepo.findAll(pageable);
	}
	
	public Review get(Integer id) throws ReviewNotFoundException {
		try {
			return reviewRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
		}
	}
	
	public void save(Review reviewInForm) {
		Review reviewInDB = reviewRepo.findById(reviewInForm.getId()).get();
		reviewInDB.setHeadline(reviewInForm.getHeadline());
		reviewInDB.setComment(reviewInForm.getComment());
		
		reviewRepo.save(reviewInDB);
		productRepo.updateReviewCountAndAverageRating(reviewInDB.getProduct().getId());
	}
	
	public void delete(Integer id) throws ReviewNotFoundException {
		if (!reviewRepo.existsById(id)) {
			throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
		}
		reviewRepo.deleteById(id);
	}
}
