package com.eshop.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eshop.common.entity.Category;

public interface CategoryRespository extends JpaRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategory(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findRootCategory(Pageable pageable);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	public long countById(Integer id);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
