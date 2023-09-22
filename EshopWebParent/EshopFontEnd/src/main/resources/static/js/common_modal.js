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