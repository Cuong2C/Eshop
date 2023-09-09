package com.eshop.admin.brand;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.FileUploadUtil;
import com.eshop.admin.category.CategoryService;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService catService;
	
	@GetMapping("/brands")
	public String listBrands(Model model) {
		
		return listByPage(1, model,"asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model, @Param("sortDir") String sortDir,@Param("keyword") String keyword ) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
		Page<Brand> page = brandService.listByPage(pageNum,sortDir, keyword );
		List<Brand> listBrands = page.getContent();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*BrandService.BRANDS_PER_PAGE +1;
		long endCount = startCount + BrandService.BRANDS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
		
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalTtems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = catService.listCategoryUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");
		
		return "/brands/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, RedirectAttributes redirect, @RequestParam("fileImg") MultipartFile multipartFile) throws IOException {	
		
		if(!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		brand.setLogo(fileName);
		Brand savedBrand= brandService.save(brand);
		String uploadDir ="../brand-logos/"+ savedBrand.getId();
		
		FileUploadUtil.cleanDir(uploadDir);
		FileUploadUtil.savefile(uploadDir, fileName, multipartFile);
		}else {
			brandService.save(brand);
		}
		redirect.addFlashAttribute("message", "The brand has been saved successfully.");
		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirect ) {
		try {
			Brand brand = brandService.get(id);
			List<Category> listCategories = catService.listCategoryUsedInForm();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("brand", brand);
			model.addAttribute("pageTitle", "Edit brand (ID: " +id +")");
			return "/brands/brand_form";
		}catch(BrandNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			return "redirect:/brands";
		}
	}
	
	@GetMapping("brands/delete/{id}")
	public String deleteBrand(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirect ) {
		try {
			brandService.delete(id);
			String brandDir = "../brand-logos/" + id;
			FileUploadUtil.removeDir(brandDir);
			redirect.addFlashAttribute("message", "The brand id " + id + " has been deleted successfully");
		}catch(BrandNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/export/csv")
	public void exportCSV(HttpServletResponse reponse) throws IOException {
		List<Brand> listBrands = brandService.listAll();
		BrandCsvExporter exporter = new BrandCsvExporter();
		exporter.export(listBrands, reponse);
	}
	
}
