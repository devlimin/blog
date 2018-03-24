layui.use('laypage', function(){
	var laypage = layui.laypage;
	laypage.render({
		elem: 'page',
		count: 10000,
		limit: 5,
		layout: ['count', 'prev', 'page', 'next', 'skip'],
		jump: function(obj){
			console.log(obj);
					//ajax
					var cur = obj.curr;
					var limit = obj.limit;
				}
			});
});
$(".list-group a").hover(
	function(){
		$(this).css({"background-color":"red", "color":"white"});
	}, 
	function(){
		$(this).css({"background-color":"white", "color":"black"});
	}
);
	
$(function () {
	//当滚动条的位置处于距顶部100像素以下时，跳转链接出现，否则消失
	$(window).scroll(function(){
		if ($(window).scrollTop()>100){
			$("#back-to-top").fadeIn(200);
		}
		else {
			$("#back-to-top").fadeOut(200);
		}
	});

	//当点击跳转链接后，回到页面顶部位置
	$("#back-to-top").click(function(){
		if ($('html').scrollTop()) {
	        $('html').animate({ scrollTop: 0 }, 100);//动画效果
	        return false;
	    }
	    $('body').animate({ scrollTop: 0 }, 100);
	    return false;
	});
});

$(document).on("click","#search-btn",function() {
	console.log($("#search-input").val());
})
