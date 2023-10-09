$(document).ready(function() {
	$("#buttonAddToCart").on("click", function() {
		
		addToCart();
	});
});

function addToCart() {
	quantity = $("#quantity" + productId).val();
	url = contextPath + "cart/add/" + productId + "/" + quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		$("#popupAddtoCart").text(response);
		 popupAddtoCart.style.display = "block";	 
		  setTimeout(function() {
            popupAddtoCart.style.display = "none";
        }, 2000);
	}).fail(function() {
		showErrorModal("Error while adding product to shopping cart.");
	});
}