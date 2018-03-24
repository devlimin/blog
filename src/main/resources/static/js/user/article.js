$(document).on("click",".comment-input a:nth-child(2)",function() {
	if($(this).parent().parent().hasClass("reply")) {
		var comment = $(".reply .comment-input textarea").val();
		if($.trim(comment) == '') {
			alert("回复不能为空")
			return false;
		}
		reply = 0;
		$(".reply .comment-input textarea").val("");
		$(".sub-comment-list").removeClass("reply");
		var html= 
			'<div class="sub-comment">' +
				'<div>' +
					'<a href="#">limin</a>：' +
					'<a href="#">@东方不败东方不败</a>' +
					'<span>'+comment+'</span>' +
				'</div>' +
				'<span>2018.02.28 16:15 </span>' +
				'<a>回复</a>' +
			'</div>';
		$(this).parent().parent().parent().append(html);
		$(this).parent().parent().remove();
	} else {
		var comment = $(".comment-input textarea").val();
		$(".comment-input textarea").val("");
		if($.trim(comment) == '') {
			alert("评论不能为空")
			return false;
		}
		reply = 0;
		$("#comment-input textarea").val("");
		$("#comment-list .comment:first").before(
			'<div class="comment">' +
				'<div class="parent-comment">' +
					'<a href="#">' +
						'<img src="https://images.nowcoder.net/head/349m.png@0e_100w_100h_0c_1i_1o_90Q_1x.png">' +
						'<span>东方不败东方不败</span>' +
					'</a>' +
					'<span>2016-03-22 16:20</span>' +
					'<span>1楼</span>' +
					'<a>回复</a>' +
					'<p>'+comment+'</p>' +
				'</div>' +
			'</div>'
		);
	}
	
})
//取消评论
$(document).on("click",".comment-input a:nth-child(3)",function() {
	$(".comment-input textarea").val("");
	if($(this).parent().parent().hasClass("reply")) {
		$(".reply").remove();
	}
	reply = 0;
})

//回复
var reply = 0;
$(document).on("click",".parent-comment a:nth-child(4), .sub-comment>a",function() {
	if(reply == 1) {
		$(".reply").remove();
		reply = 0;
		return false;
	}
	reply++;
	var parent = $(this).parent();

	var html = 
				'<div class="comment-write reply">' +
					'<form class="comment-input">'+
						'<textarea class="form-control" placeholder="写下你的评论..."></textarea>' + 
						'<a>发送</a>' +
						'<a>取消</a>' +
					'</form>' +
					'<div style="clear: both;"></div>' +
				'</div>';
	//评论回复
	if(parent.hasClass("parent-comment")) {
		var sub = parent.next(".sub-comment-list");
		if (sub.length == 0) {
			html = '<div class="sub-comment-list reply">' + html +'</div>';
			$(parent).parent().append(html);
			
		} else {
			sub.append(html)
		}
	}
	//子回复
	if(parent.hasClass("sub-comment")) {
		$(parent).parent().append(html);
	}  
})





layui.use('laypage', function(){
	var laypage = layui.laypage;
	laypage.render({
		elem: 'page',
		count: 10000,
		limit: 5,
		layout: ['count', 'prev', 'page', 'next', 'skip'],
		jump: function(obj){
				//ajax
				var cur = obj.curr;
				var limit = obj.limit;
			}
		});
});