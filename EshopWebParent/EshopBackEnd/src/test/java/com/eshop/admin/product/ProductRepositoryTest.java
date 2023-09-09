package com.eshop.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		
		Product product = new Product();
		product.setName("Acer desktop");
		product.setAlias("acer_desktop");
		product.setShortDescription("a new product from Acer ");
		product.setFullDescription("a 4K desktop ...");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(650);
		product.setCost(300);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAll() {
		Iterable<Product> listProducts = repo.findAll();
		listProducts.forEach(System.out::println);
	}
	
	@Test 
	public void testGetProduct() {
		Product product = repo.findById(1).get();
		System.out.println(product);

	}
	
	@Test
	public void testUpdateProduct() {
		Product product = repo.findById(1).get();
		product.setPrice(567);
		repo.save(product);
	}
	@Test
	public void testDeleteProduct() {
		repo.deleteById(3);
		
	}
	
	@Test 
	public void testSaveProductWithImage() {
		Product product = repo.findById(1).get();
		product.setMainImage("main_image.jpg");
		product.addExtraImage("extra_image1.jpg");
		product.addExtraImage("extra_image2.jpg");
		product.addExtraImage("extra_image3.jpg");

		repo.save(product);
	}
	
	@Test 
	public void testSaveProductWithDetails() {
		Product product = repo.findById(1).get();
		product.addDetail("Memory", "128 GB");
		product.addDetail("CPU model", "G10");
		product.addDetail("OS", "Android 12");
		
		repo.save(product);
	}
	
	
}
