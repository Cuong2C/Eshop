package com.eshop.admin.setting;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepoTest {

	@Autowired 
	private SettingRepository repo;
	
	@Test
	public void testCreated() {
//		Setting setting = new Setting("SITE_NAME", "Eshop", SettingCategory.GENERAL);
//		Setting setting = new Setting("SITE_LOGO", "Eshop.png", SettingCategory.GENERAL);
//		Setting setting2 = new Setting("COPYRIGHT", "Copyright (C) Eshop Ltd.", SettingCategory.GENERAL);
		Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
		Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Setting decimalPoitType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
		Setting thoudsandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
		
		
		
		repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPoitType, decimalDigits, thoudsandsPointType));
	}
	
	@Test
	public void testListSettingsByCategory() {
		List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);
		
		settings.forEach(System.out::println);
	}
}
