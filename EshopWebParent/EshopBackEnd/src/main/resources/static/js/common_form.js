$(document).ready(function(){
		$("#cancelbtn").on("click", function(){
		window.location = moduleURL;
		});
		
		$("#fileImg").change(function(){
			if(!checkFileSize(this)){
				return;
			}
				showImgThumnail(this);
		});
	});
	
	function checkFileSize(fileInput){
	fileSize = fileInput.files[0].size;
		
		if(fileSize > 1048576){
			fileInput.setCustomValidity("Please choose an image less than 1MB!");
			fileInput.reportValidity();
			return false;
		}else{
			fileInput.setCustomValidity("");
			
			return true;
		}
}
	
	function showImgThumnail(fileInput){
		var file = fileInput.files[0];
		var reader = new FileReader();
		reader.onload = function(e){
			$("#thumbnail").attr("src",e.target.result);
		};
		reader.readAsDataURL(file);
	}
	
	function showModalDialog(message){
		$("#modalBody").text(message);
		$("#modalDialog").modal();
	}
	
	function showErrorModal(message){
		showModalDialog(message);
	}
	function showWarningModal(message){
		showModalDialog(message);
	}
	