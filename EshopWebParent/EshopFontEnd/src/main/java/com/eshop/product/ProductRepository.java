package com.eshop.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eshop.common.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query("SELECT p FROM Product p WHERE p.enabled = true AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%) ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND MATCH(name, short_description,full_description) AGAINST (?1)", nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
	@Query("UPDATE Product p SET p.averageRating = (SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = ?1),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1) WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	
}
