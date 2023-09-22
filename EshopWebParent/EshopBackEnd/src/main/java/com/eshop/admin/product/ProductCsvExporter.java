package com.eshop.admin.product;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Product;

import jakarta.servlet.http.HttpServletResponse;

public class ProductCsvExporter extends AbstractExporter {
	
	public void export(List<Product> listProducts, HttpServletResponse reponse) throws IOException {
		super.setResponseHeader(reponse, "text/csv", ".csv","products_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(reponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"ID","Product Name","Category", "Cost", "Price", "DiscountPercent"};
		String[] fieldMapping = {"id", "name","category", "cost", "price", "discountPercent" };
		
		csvWritter.writeHeader(csvHeader);
		
		for(Product product:listProducts) {
			csvWritter.write(product, fieldMapping);
		}
		
		csvWritter.close();
	}
}
