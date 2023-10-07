package com.eshop.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.order.OrderTrack;
import com.eshop.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 10;
	
	@Autowired 
	private OrderRepository repo;
	
	@Autowired 
	private CountryRepository countryRepo;
	
	public Page<Order> listByPage(int pageNum, String sortField, String sortDir, String keyword) {

		Sort sort = null;
		
		if ("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		} else {
			sort = Sort.by(sortField);
		}
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		} else {
			return repo.findAll(pageable);
		}
		
	}
	
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id); 
		}
		
		repo.deleteById(id);
	}	
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}	
	
	public void save(Order orderInForm) {
		Order orderInDB = repo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		
		repo.save(orderInForm);
	}	
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = repo.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		
		if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDB.getOrderTracks();
			
			OrderTrack track = new OrderTrack();
			track.setOrder(orderInDB);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			track.setNotes(statusToUpdate.defaultDescription());
			
			orderTracks.add(track);
			
			orderInDB.setStatus(statusToUpdate);
			
			repo.save(orderInDB);
		}
		
	}
}
