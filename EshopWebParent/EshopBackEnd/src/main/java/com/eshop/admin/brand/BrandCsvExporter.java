package com.eshop.admin.brand;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.Brand;

import jakarta.servlet.http.HttpServletResponse;

public class BrandCsvExporter extends AbstractExporter {
	
	public void export(List<Brand> listBrands, HttpServletResponse reponse) throws IOException {
		super.setResponseHeader(reponse, "text/csv", ".csv","brands_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(reponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Brand ID", "Brand Name", "Categories"};
		String[] fieldMapping = {"id", "name","categories"};
		
		csvWritter.writeHeader(csvHeader);
		
		for(Brand brand:listBrands) {
			csvWritter.write(brand, fieldMapping);
		}
		
		csvWritter.close();
	}
}
