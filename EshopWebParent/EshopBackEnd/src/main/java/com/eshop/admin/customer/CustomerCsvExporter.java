package com.eshop.admin.customer;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.Customer;

import jakarta.servlet.http.HttpServletResponse;

public class CustomerCsvExporter extends AbstractExporter {
	
	public void export(List<Customer> listCustomers, HttpServletResponse reponse) throws IOException {
		super.setResponseHeader(reponse, "text/csv", ".csv","customer_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(reponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"ID","First Name","Last Name", "Email", "City", "State", "Country"};
		String[] fieldMapping = {"id", "firstName","lastName", "email", "city", "state", "country" };
		
		csvWritter.writeHeader(csvHeader);
		
		for(Customer customer:listCustomers) {
			csvWritter.write(customer, fieldMapping);
		}
		
		csvWritter.close();
	}
}
