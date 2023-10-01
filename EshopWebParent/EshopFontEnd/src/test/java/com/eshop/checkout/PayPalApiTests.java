package com.eshop.checkout;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.eshop.checkout.paypal.PaypalOrderResponse;

public class PayPalApiTests {

	private static final String BASE_URL = "https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API = "/v2/checkout/orders/";
	private static final String CLIENT_ID = "Ad94pBgPhYci4UkqkU_Gh2V_KTjSXniYg-A1o1Owd8Dg3iwWQxugp8gflgbZGDw5Rt76UdElZQGngVc-";
	private static final String CLIENT_SECRET = "EB2xqnzVFNsLWLyU1egi-ELYh0mfqzbglfVt9dOEDjsNY-N3g7WNPe5WRuEWK5IfsLi6H1uqSt4RakLx";
	
	@Test
	public void testGetOrderDetails() {
		String orderId = "4YY37611U55950747";
		String requestURL = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<PaypalOrderResponse> response = restTemplate.exchange(
				requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);
		PaypalOrderResponse orderResponse = response.getBody();

		System.out.println("Order ID: " + orderResponse.getId());
		System.out.println("Validated: " + orderResponse.validate(orderId));
		
	}
}
