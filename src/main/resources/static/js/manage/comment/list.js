$(function() {
    $(".comment").mouseover(function(){
        $(this).find(".action")[0].style.display="block";
    })
    $(".comment").mouseout(function(){
        $(this).find(".action")[0].style.display="none";
    })

$(document).on("click",".comment-btn",function() {
	console.log($(this).parent().parent().next(".detail").children(".reply-div").length);
	if($(this).parent().parent().next(".detail").children(".reply-div").length != 0) {
		$(this).parent().parent().next(".detail").children(".reply-div").remove();
		return false;
	}
	$(this).parent().parent().next(".detail")
					.append('<div class="pull-right reply-div" style="margin-top:15px;">'+
								'<input type="text" class="form-control reply-input" style="width:780px;display:inline-block"/>' +
								'<input type="button" class="btn btn-default reply-btn" style="display:inline-block;margin-left:15px;margin-right:10px;" value="回复评论">'+
							'</div><div style="clear:both"></div>')
});
$(document).on("click",".reply-btn",function() {
	var reply = $(this).prev(".reply-input").val();
	console.log(reply);
	$(this).parent().remove();
})
$(document).on("click",".comment-del",function() {
	$(this).parent().parent().parent().remove();
});
})