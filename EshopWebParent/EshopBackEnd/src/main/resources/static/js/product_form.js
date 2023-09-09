var extraImageCount = 0;
dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function(){
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();

	dropdownBrands.change(function(){
		dropdownCategories.empty();
		getCategories();
	});
	getCategoriesForNewForm();
	
	$("input[name='extraImage']").each(function(index){
		extraImageCount++;
		$(this).change(function(){
			if(!checkFileSize(this)){
				return;
			}
			showExtraImgThumnail(this, index);
		});		
	});
	
	$("a[name='linkRemoveExtraImage']").each(function(index){
		$(this).click(function(){
			removeExtraImage(index);
		});
	});
	
	$("i[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		});
	});
});

function getCategoriesForNewForm(){
	catIdField = $("#categoryId");
	editMode = false;
	if(catIdField.length){
		editMode = true;
	}
	if(!editMode){
		getCategories();
	}
}

function showExtraImgThumnail(fileInput, index){
	var file = fileInput.files[0];
	
		fileName = file.name;
		imageNameHiddenField = $("#imageName" + index);
		if(imageNameHiddenField.length){
			imageNameHiddenField.val(fileName);
		}
		
		var reader = new FileReader();
		reader.onload = function(e){
			$("#extraThumbnail" + index).attr("src",e.target.result);
		};
		reader.readAsDataURL(file);
		if(index >= extraImageCount -1){
		addNextExtraImageSection(index + 1);
		}
}

function addNextExtraImageSection(index){
	htmlExtra = '<div class="col border m-3 p-2" id="divExtraImage'+index+'">' +
				'<div id="extraImageHeader'+index+'"><label >Extra Image #'+(index +1)+':</label></div>'+
				'<div class="m-2">'+
					'<img id="extraThumbnail'+index+'" alt="Extra image #'+ (index + 1) +' preview" class="img-fluid" src="/EshopAdmin/images/defaultCate.jpg" style="width: auto; height: 180px;"/>'+
				'</div>'+
				'<div>'+
					'<input type="file" name="extraImage" onchange="showExtraImgThumnail(this, '+index+')" accept="image/png, image/jpeg"/>'+
				'</div>'+		
			'</div>';
			
			htmlLinkRemove = '<a class="fa-regular fa-rectangle-xmark fa-2x icon-red float-right" title="Remove this image" href="javascript:removeExtraImage('+(index -1)+')"></a>';
			$("#divProductImages").append(htmlExtra);
			$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
			extraImageCount++;
}

function removeExtraImage(index){
	$("#divExtraImage" + index).remove();
}


function getCategories(){
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";
	$.get(url, function(responseJson){
		$.each(responseJson, function(index, category){
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		});
	});
}

function checkUnique(form){
	
	productName = $("#name").val();
	productId = $("#id").val();
	csrfValue = $("input[name='_csrf']").val();
	params = {id: productId, name: productName,_csrf: csrfValue};
	
	$.post(checkUniqueUrl, params, function(response){
		if(response=="OK"){
			form.submit();
		}else if(response=="Duplicated"){
			showWarningModal("There is another product have the same name: "+ productName);
		}else{
			showErrorModal("Unknown response from server");
		}
	}).fail(function(){
			showErrorModal("Could not connect to the server");
	});
	
	return false;
}

function addNextDetailSection(){
	allDivDetails = $("[id^='divDetail']");
	divDetailCount = allDivDetails.length;

	
	htmlDetailSection = '<div class="form-inline" id="divDetail'+ divDetailCount +'">'+
			'<input type="hidden"  name="detailIDs" value="0"/>'+
			'<label class="m-3" >Name:</label>'+
			'<input type="text" class="form-control w-25" name="detailNames"  maxlength="255"/>'+
			
			'<label class="m-3">Value:</label>'+
			'<input type="text" class="form-control w-25" name="detailValues"  maxlength="255"/>'+
			
		'</div>';
		$("#divProductDetails").append(htmlDetailSection);
		
		previousDivDetailSection = allDivDetails.last();
		previousDivDetailID = previousDivDetailSection.attr("id");
		
		htmlLinkRemove = '<i class="fa-solid fa-xmark fa-2x icon-red ml-2" title="Remove this detail" onclick="removeDetailSectionById('+previousDivDetailID+')"></i>';
		
		previousDivDetailSection.append(htmlLinkRemove);
		$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id){
	$(id).remove();
}

function removeDetailSectionByIndex(index){
	$("#divDetail" + index).remove();
}
