package com.eshop.admin.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eshop.common.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	public Product findByName(String name);
	
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Long countById(Integer Id);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% OR p.shortDescription LIKE %?1% OR p.fullDescription LIKE %?1% OR p.brand.name LIKE %?1% OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%")
	public Page<Product> findAllCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% OR p.shortDescription LIKE %?3% OR p.fullDescription LIKE %?3% OR p.brand.name LIKE %?3% OR p.category.name LIKE %?3%)")
	public Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
	public Page<Product> searchProductsByName(String keyword, Pageable pageable);
	
	@Query("UPDATE Product p SET p.averageRating = (SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = ?1),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	
}
