$(document).on("click","#search-btn",function() {
	console.log($("#search-input").val());
})
$(document).on("click",".article .info .right a:nth-child(4)",function() {
	$(this).parent().parent().parent().remove();
})