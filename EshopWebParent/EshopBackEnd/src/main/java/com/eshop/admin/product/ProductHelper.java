package com.eshop.admin.product;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eshop.admin.AmazonS3Util;
import com.eshop.common.entity.product.Product;
import com.eshop.common.entity.product.ProductImage;

public class ProductHelper {


	static void deleteExtraImagesDeletedOnForm(Product product) {
		String extraImageDir = "product-images/" + product.getId() + "/extras";
		List<String> listObjectKeys =  AmazonS3Util.listFolder(extraImageDir);
		
		for (String objectKey : listObjectKeys) {
			int lastIndexOfSlash = objectKey.lastIndexOf("/");
			String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());
			
			if (!product.containImageName(fileName)) {
				AmazonS3Util.deleteFile(objectKey);
				System.out.println("Deleted extra image: " + objectKey);
			}
		}
		
	}

	static void setExistingExtraImgName(String[] imageIDs, String[] imageNames, Product product) {
		if(imageIDs == null || imageIDs.length == 0) return;
		Set<ProductImage> images = new HashSet<>();
		
		for(int count = 0; count < imageIDs.length; count ++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			images.add(new ProductImage(id, name, product));
		}
		product.setImages(images);
		
	}

	static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count ++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			
			if(id != 0) {
				product.addDetail(id, name, value);
			}else if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	static void saveUploadImage(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts,
			Product savedProduct) throws IOException {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());

			String uploadDir = "product-images/" + savedProduct.getId();

			List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDir + "/");
			for (String objectKey : listObjectKeys) {
				if (!objectKey.contains("/extras/")) {
					AmazonS3Util.deleteFile(objectKey);
				}
			}
			AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipart.getInputStream());
		}
		if (extraImageMultiparts.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty())
					continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			}
		}
	}

	static void setNewExtraImageName(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					if(!product.containImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}

	}

	static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
}
