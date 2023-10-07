

$(document).ready(function() {
	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});
});

function voteReview(currentLink) {
	requestURL = currentLink.attr("href");

	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult) {
		if(voteResult.successful == false){
			showModalDialog(voteResult.message);
		}
		updateVoteCountAndIcons(currentLink, voteResult);


		}).fail(function() {
		showErrorModal("Error voting review.");
	});	
}

function updateVoteCountAndIcons(currentLink, voteResult) {
	reviewId = currentLink.attr("reviewId");
	voteUpLink = $("#linkVoteUp-" + reviewId);

	
	$("#voteCount-" + reviewId).text(voteResult.voteCount + " Votes");
	
	message = voteResult.message;
	
	if (message.includes("successfully voted")) {
		highlightVoteUpIcon(currentLink);
	} else if (message.includes("unvote")) {
		unhighlightVoteUpIcon(voteUpLink);
	}
}

function highlightVoteUpIcon(voteUpLink) {
	voteUpLink.removeClass("far").addClass("fas");
}

function unhighlightVoteUpIcon(voteUpLink) {
	voteUpLink.removeClass("fas").addClass("far");	
}