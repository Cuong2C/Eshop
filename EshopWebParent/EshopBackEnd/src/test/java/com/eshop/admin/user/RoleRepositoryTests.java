package com.eshop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.eshop.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository repo;
	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Amin", "manage everything");
		Role saveRole = repo.save(roleAdmin);
		assertThat(saveRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateOrtherRoles() {
		Role roleSalesperson = new Role("Salesperson", "manage product price, customer, shipping, orders & sales report");
		Role roleEditor = new Role("Editor", "manage categories, brands, products, articles & menus");
		Role roleShipper = new Role("Shipper", "view products, view orders & update order status");
		Role roleAssistant = new Role("Assistant", "manage questions & reviews");
		
		repo.saveAll(List.of(roleSalesperson,roleEditor,roleShipper,roleAssistant));
	}
}
