package com.eshop.admin.user.exporter;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.eshop.admin.AbstractExporter;
import com.eshop.common.entity.User;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class excelExporter extends AbstractExporter{
	private XSSFWorkbook workBook ;	
	private XSSFSheet sheet;
	
	public excelExporter() {
		 workBook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		 sheet = workBook.createSheet("users");
		 XSSFRow row = sheet.createRow(0);
		 
		 XSSFCellStyle cellStyle = workBook.createCellStyle();
		 XSSFFont font = workBook.createFont();
		 font.setBold(true);
		 font.setFontHeight(14);
		 cellStyle.setFont(font);
		 
		 createCell(row, 0, "User ID",cellStyle );
		 createCell(row, 1, "Email",cellStyle );
		 createCell(row, 2, "First Name",cellStyle );
		 createCell(row, 3, "Last Name",cellStyle );
		 createCell(row, 4, "Roles",cellStyle );
		 createCell(row, 5, "Enabled",cellStyle );
		 
	}
	
	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);
		
		if(value instanceof Integer) {
			cell.setCellValue((Integer) value);
		}else if(value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		}else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}
	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/octet-stream", ".xlsx", "users_");
		
		writeHeaderLine();
		writeDataLine(listUsers);
		
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		workBook.close();
		outputStream.close();
	}

	private void writeDataLine(List<User> listUsers) {
		int rowIndex =1;
		
		 XSSFCellStyle cellStyle = workBook.createCellStyle();
		 XSSFFont font = workBook.createFont();
		 font.setFontHeight(14);
		 cellStyle.setFont(font);
		for(User user: listUsers) {
			XSSFRow row = sheet.createRow(rowIndex++);
			
			int columnIndex =0;
			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
			
		}
		
	}
}
