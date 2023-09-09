package com.eshop.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Product;
import com.eshop.common.exception.ProductNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {
	
	public static final int PRODUCTS_PER_PAGE = 10;
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> listAll(){
		return repo.findAll();
	}
	
	public Page<Product> listByPage(int pageNum,String sortField, String sortDir, String keyword, Integer categoryId){
		Sort sort = Sort.by(sortField);
		
		if(sortDir.equals("asc")) {
			sort = sort.ascending();
		}else if(sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		Pageable pageable = PageRequest.of(pageNum -1, PRODUCTS_PER_PAGE, sort);
		if(keyword != null) { 
			if(categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
			return repo.findAll(keyword, pageable);
		}
		
		if(categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllCategory(categoryId, categoryIdMatch, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	public Product save(Product product) {
		if(product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		}else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		product.setUpdatedTime(new Date());
		return repo.save(product);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);
		if(isCreatingNew) {
			if(productByName != null) return "Duplicated";
		}else{
			if(productByName != null && productByName.getId() != id ) return "Duplicated";
		}
		
		return "OK";
	}
	
	public void updateProductEnableStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = repo.countById(id);
		if(countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any products with id " + id);
		}
		repo.deleteById(id);
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find any product with id "+id);
		}
	}
	
	public void saveProductSalesPerson(Product productInFrom) {
		Product productInDB = repo.findById(productInFrom.getId()).get();
		productInDB.setCost(productInFrom.getCost());
		productInDB.setPrice(productInFrom.getPrice());
		productInDB.setDiscountPercent(productInFrom.getDiscountPercent());
		
		repo.save(productInDB);
	}
		
		
}
