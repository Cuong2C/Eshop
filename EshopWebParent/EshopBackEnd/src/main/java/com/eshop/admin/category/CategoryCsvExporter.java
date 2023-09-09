package com.eshop.admin.category;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCsvExporter extends AbstractExporter {
	
	public void export(List<Category> listCategories, HttpServletResponse reponse) throws IOException {
		super.setResponseHeader(reponse, "text/csv", ".csv","categories_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(reponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"Category ID", "Category Name"};
		String[] fieldMapping = {"id", "name"};
		
		csvWritter.writeHeader(csvHeader);
		
		for(Category category:listCategories) {
			category.setName(category.getName().replace("--", "  "));
			csvWritter.write(category, fieldMapping);
		}
		
		csvWritter.close();
	}
}
