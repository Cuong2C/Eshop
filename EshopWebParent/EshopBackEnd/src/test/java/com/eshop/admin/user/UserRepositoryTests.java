package com.eshop.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.eshop.admin.category.CategoryRespository;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Role;
import com.eshop.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private CategoryRespository repoC;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userDavid = new User("david@gmail.com", "password", "David", "Beckham");
		
		userDavid.addRole(roleAdmin);
		User saveUser = repo.save(userDavid);
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateUserWithTwoRoles() {
		User userGoku = new User("goku@gmail.com", "password", "Goku", "Son");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		
		userGoku.addRole(roleEditor);
		userGoku.addRole(roleAssistant);
		User saveUser = repo.save(userGoku);
		assertThat(saveUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUser() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user->System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User user = repo.findById(1).get();
		System.out.println(user);
		
	}
	
	@Test
	public void testUpdateUser() {
		User user = repo.findById(1).get();
		user.setEnabled(true);
		repo.save(user);
	}
	
	@Test
	public void testUpdateUserRole() {
		User user = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		
		user.getRoles().remove(roleEditor);
		user.addRole(roleSalesperson);
		repo.save(user);
		
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId=2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "goku@gmail.com";
		User user = repo.getUserbyEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test 
	public void testCountById() {
		Integer id = 4;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id =1;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id =3;
		repo.updateEnabledStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNum =1;
		int pageSize = 5;
		
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers =  page.getContent();
		
		listUsers.forEach(user->System.out.println(user));
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUser() {
		String keyword ="meo";
		
		int pageNum =0;
		int pageSize = 5;
		
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers =  page.getContent();
		
		listUsers.forEach(user->System.out.println(user));
		assertThat(listUsers.size()).isGreaterThan(0);
	}
	
	@Test
	private void testFindByName() {
		String name = "Computers11";
		Category category = repoC.findByName(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
}
