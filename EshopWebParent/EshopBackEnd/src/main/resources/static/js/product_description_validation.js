function checkUnique(form) {
    var shortDescription = form.shortDescription.value;
    var fullDescription = form.fullDescription.value;
    
    if (shortDescription.length > 512) {
        showModalDialog("Short description must not exceed 512 characters");
        return false;
    }
    
    if (fullDescription.length > 4096) {
        showModalDialog("Full description must not exceed 4096 characters");
        return false;
    }
       return true;
}
    