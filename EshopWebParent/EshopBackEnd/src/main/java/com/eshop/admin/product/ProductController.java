package com.eshop.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eshop.admin.AmazonS3Util;
import com.eshop.admin.brand.BrandService;
import com.eshop.admin.category.CategoryService;
import com.eshop.admin.security.EshopUserDetails;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.product.Product;
import com.eshop.common.exception.ProductNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "id", "asc", null, null);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model, String sortField, String sortDir, String keyword, @Param("categoryId") Integer categoryId ) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}	
	
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword , categoryId );
		List<Product> listProducts = page.getContent();
		List<Category> listCategories = categoryService.listCategoryUsedInForm();
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum-1)*ProductService.PRODUCTS_PER_PAGE +1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE -1;
		if(endCount>page.getTotalElements()) endCount = page.getTotalElements();
		if(categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalTtems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listCategories", listCategories);
		
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();

		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create a new product");
		return "products/product_form";
	}

	@PostMapping("products/save")
	public String saveProduct(Product product, RedirectAttributes redirect,
			@RequestParam(value = "fileImg", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal EshopUserDetails loggedUser
			) throws IOException {
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductSalesPerson(product);
				redirect.addFlashAttribute("message", "The product has been saved successfully.");			
				return "redirect:/products";
			}
		}
		
		ProductHelper.setMainImageName(mainImageMultipart, product);
		ProductHelper.setExistingExtraImgName(imageIDs, imageNames, product);
		ProductHelper.setNewExtraImageName(extraImageMultiparts, product);
		ProductHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

		Product savedProduct = productService.save(product);

		ProductHelper.saveUploadImage(mainImageMultipart, extraImageMultiparts, savedProduct);

		ProductHelper.deleteExtraImagesDeletedOnForm(product);
		
		redirect.addFlashAttribute("message", "The product has been saved successfully");
		return "redirect:/products";
	}


	@GetMapping("/products/{id}/enabled/{status}")
	public String updateEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirect) {
		productService.updateProductEnableStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		redirect.addFlashAttribute("message", message);
		return "redirect:/products";
	}

	@GetMapping("products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			productService.delete(id);
			String productExtraImgDir = "product-images/" + id + "/extras";
			AmazonS3Util.removeFolder(productExtraImgDir);
			
			String productImgDir = "product-images/" + id;
			AmazonS3Util.removeFolder(productImgDir);
			
			redirect.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect, @AuthenticationPrincipal EshopUserDetails loggedUser) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberExistExtraImg = product.getImages().size();
			
			boolean isForSalesperson = false;
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					 isForSalesperson = true;
				}
			}
			
			model.addAttribute("isForSalesperson", isForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit product ID: " + id);
			model.addAttribute("numberExistExtraImg", numberExistExtraImg);
			
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/products";
		}
	}
	
	@GetMapping("products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirect) {
		try {
			Product product = productService.get(id);
			
			model.addAttribute("product", product);
	
			return "/products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirect.addFlashAttribute("message", e.getMessage());
			
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/export/csv")
	public void exportCSV(HttpServletResponse reponse) throws IOException {
		List<Product> listProducts = productService.listAll();
		ProductCsvExporter exporter = new ProductCsvExporter();
		exporter.export(listProducts, reponse);
	}
	
}
