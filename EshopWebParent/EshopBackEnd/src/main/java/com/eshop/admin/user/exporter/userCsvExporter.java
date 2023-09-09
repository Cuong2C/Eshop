package com.eshop.admin.user.exporter;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class userCsvExporter extends AbstractExporter {
	
	public void export(List<User> listUsers, HttpServletResponse reponse) throws IOException {
		super.setResponseHeader(reponse, "text/csv", ".csv","users_");
		
		ICsvBeanWriter csvWritter = new CsvBeanWriter(reponse.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
		String[] fieldMapping = {"id", "email","firstName", "lastName", "roles","enabled"};
		
		csvWritter.writeHeader(csvHeader);
		
		for(User user:listUsers) {
			csvWritter.write(user, fieldMapping);
		}
		
		csvWritter.close();
	}
}
