package com.eshop.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eshop.common.entity.Review;
import com.eshop.common.entity.order.OrderDetail;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.product.Product;
import com.eshop.common.exception.ProductNotFoundException;
import com.eshop.order.OrderDetailRepository;

@Service
public class ProductService {

	public static final int PRODUCTS_PER_PAGE = 12;
	
	public static final int SEARCH_RESULTS_PER_PAGE = 12;
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private OrderDetailRepository orderDetailRepo;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryId){
		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByCategory(categoryId, categoryIdMatch, pageable);
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if(product == null) {
			throw new ProductNotFoundException("Could not find any products with alias " + alias);
		}
		return product;
	}
	public Product getProduct(Integer id) throws ProductNotFoundException {
		try {
			Product product = repo.findById(id).get();
			return product;
		} catch (NoSuchElementException e) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}	
	
	public Page<Product> search(String keyword, int pageNum){
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pageable);
	}
	
	public List<Product> list8MostSalesProduct() {
		List<Product> list8MostProductSold = new ArrayList<>();
		
		List<OrderDetail> listOrderDetail = orderDetailRepo.listAllOrderDetailDelivered(OrderStatus.DELIVERED);
		
		 List<QuantityProductDelivered> listProductIdSold = new ArrayList<>() ;
		
		for(OrderDetail orderDetail: listOrderDetail) {
			QuantityProductDelivered quantityProductDelivered = new QuantityProductDelivered();
			quantityProductDelivered.setProduct_id(orderDetail.getProduct().getId());
			quantityProductDelivered.setQuantity_delivered(orderDetail.getQuantity());
			
			listProductIdSold.add(quantityProductDelivered);		
		}
		
		List<QuantityProductDelivered> listTotalEachProductSold = new ArrayList<>() ;
		
		for(QuantityProductDelivered quanPD:listProductIdSold) {
			if(listTotalEachProductSold == null || listTotalEachProductSold.isEmpty()) {
				
				addQuantityDelivered(quanPD, listProductIdSold);
				
				listTotalEachProductSold.add(quanPD);
			}else {
				boolean isDuplicate = true;
				
				for(QuantityProductDelivered quanPD3:listTotalEachProductSold) {
					if(quanPD.getProduct_id().equals(quanPD3.getProduct_id())) {
						isDuplicate = true;
						break;
					}else {
						isDuplicate = false;
					}
				}
				
				if(!isDuplicate) {
					addQuantityDelivered(quanPD, listProductIdSold);
					listTotalEachProductSold.add(quanPD);
				}
			}
		}
		
		listTotalEachProductSold.sort((o1, o2) -> o2.getQuantity_delivered() - o1.getQuantity_delivered());
		
		System.out.println(listTotalEachProductSold);
		
		for(int i=0; i < 8 && i < listTotalEachProductSold.size(); i++) {
			
			
			Product product = repo.findById(listTotalEachProductSold.get(i).getProduct_id()).get();
			
			list8MostProductSold.add(product);
		}
		return list8MostProductSold;		
	}

	private void addQuantityDelivered(QuantityProductDelivered quanPD, List<QuantityProductDelivered> listProductIdSold) {
		quanPD.setQuantity_delivered(0);
		for(QuantityProductDelivered quanPD2:listProductIdSold) {
			if(quanPD.getProduct_id().equals(quanPD2.getProduct_id())) {
				quanPD.setQuantity_delivered(quanPD.getQuantity_delivered() + quanPD2.getQuantity_delivered());
			}
		}
	}

}
