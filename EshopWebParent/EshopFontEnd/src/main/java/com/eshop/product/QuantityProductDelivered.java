package com.eshop.product;

public class QuantityProductDelivered {
	private Integer product_id;
	private int quantity_delivered;
	
	public QuantityProductDelivered() {
	}

	public QuantityProductDelivered(Integer product_id, int quantity_delivered) {
		this.product_id = product_id;
		this.quantity_delivered = quantity_delivered;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public int getQuantity_delivered() {
		return quantity_delivered;
	}

	public void setQuantity_delivered(int quantity_delivered) {
		this.quantity_delivered = quantity_delivered;
	}

	@Override
	public String toString() {
		return "QuantityProductDelivered [product_id=" + product_id + ", quantity_delivered=" + quantity_delivered
				+ "]";
	}
	
	
	
}
