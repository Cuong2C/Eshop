package com.eshop.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	private BrandRepository repo;
	
	@Test
	public void testCreateBrands() {
		Category categories = new Category(4);
		Category categories2 = new Category(7);
		
		Category categories3 = new Category(29);
		Category categories4 = new Category(24);
		
		Set<Category> listApple = new HashSet<>();
		listApple.add(categories);
		listApple.add(categories2);
		
		Set<Category> listSamsung = new HashSet<>();
		listSamsung.add(categories3);
		listSamsung.add(categories4);
		
		Brand apple = new Brand("Apple","brand-logo.png",listApple);
		Brand samsung = new Brand("Samsung","brand-logo.png",listSamsung);
		repo.save(apple);
		repo.save(samsung);
		
		assertThat(apple.getId()).isGreaterThan(0);
		assertThat(samsung.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testFindAllBrand() {
		Iterable<Brand> listBrands = repo.findAll();
		listBrands.forEach(System.out::println);
		assertThat(listBrands).isNotEmpty();
	}
	
	@Test
	public void testGetBrabdById() {
		Brand brand = repo.findById(1).get();
		System.out.println(brand);
	}
	
	@Test
	public void testUpdateBrand() {
		Brand brand = repo.findById(3).get();
		
		brand.setName("Samsung Electronics");
	}
	
	@Test
	public void testDeleteBrand() {
		repo.deleteById(2);
	}
	
}
