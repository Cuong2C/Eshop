package com.eshop.admin.setting;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.AmazonS3Util;
import com.eshop.admin.FileUploadUtil;
import com.eshop.common.Constants;
import com.eshop.common.entity.Currency;
import com.eshop.common.entity.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SettingController {

	@Autowired
	private SettingService service;
	
	@Autowired
	private CurrencyRepository currencyRepo;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = service.listAllSetting();
		List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
		
		return "settings/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImg") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes redirect) throws IOException {
		GeneralSettingBag settingBag = service.getGeneralSettings();
		
		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);
		updateSettingValuesForm(request, settingBag.list());
		
		redirect.addFlashAttribute("message", "General settings have been saved.");
		
		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);
				
			String uploadDir = "site-logo";
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		}
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);
		
		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	
	private void updateSettingValuesForm(HttpServletRequest request, List<Setting> listSettings) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}
		
		service.saveAll(listSettings);
	}
	
	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSetttings(HttpServletRequest request, RedirectAttributes redirect) {
		List<Setting> mailServerSettings = service.getMailServerSettings();
		updateSettingValuesForm(request, mailServerSettings);
		
		redirect.addFlashAttribute("message", "Mail server settings have been saved");
		
		return "redirect:/settings#mailServer";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSetttings(HttpServletRequest request, RedirectAttributes redirect) {
		List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
		updateSettingValuesForm(request, mailTemplateSettings);
		
		redirect.addFlashAttribute("message", "Mail template settings have been saved");
		
		return "redirect:/settings#mailTemplates";
	}
	
	@PostMapping("/settings/save_payment")
	public String savePaymentSetttings(HttpServletRequest request, RedirectAttributes redirect) {
		List<Setting> paymentSettings = service.getPaymentSettings();
		updateSettingValuesForm(request, paymentSettings);
		
		redirect.addFlashAttribute("message", "Payment settings have been saved");
		
		return "redirect:/settings#payment";
	}		
	
	
	
}
