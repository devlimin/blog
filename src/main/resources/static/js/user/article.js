$(function () {
	var total=0;
	layui.use(['laypage'], function() {
        var laypage = layui.laypage;
        page(1,10);
        function page(pageNum, pageSize) {
			$.ajax({
				url:"/comment/list/",
				data:"aid="+$("#aid").val()+"&pageNum="+pageNum+"&pageSize="+pageSize,
				type:"get",
				success:function (resp) {
					if(resp.code==0) {
						html = "";
						var data = resp.data;
                        if(data == null || data.list == null || data.list.length == 0) {
                            html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                        } else {
                        	var endRow = data.total-(pageNum-1)*pageSize;
                            $.each(data.list,function (i, commentVo) {
                                html +='<div class="comment" id="'+commentVo.comment.id+'">' +
											'<div class="parent-comment">' +
												'<a class="headimg" href="/article/list/'+commentVo.user.id+'">' +
													'<img src="'+commentVo.user.headUrl+'">' +
												'</a>' +
												'<div class="info">' +
													'<a href="/article/list/'+commentVo.user.id+'">'+commentVo.user.name+'</a>' +
													'<span style="font-size: 12px;color: #969696;">'+(endRow--)+'楼 '+new Date(commentVo.comment.releaseDate).format()+'</span>' +
												'</div>'+
												'<div class="comment-wrap">' +
													'<p>'+commentVo.comment.content+'</p>' +
													'<div class="tool">' +
														'<a class="layui-icon" style="font-size: 12px;color: #969696;">&#xe6c6; 12人赞 </a>'+
														'<a class="reply-input">回复</a>' +
														'<a class="report">举报</a>'+
													'</div>'+
                                    			'</div>'+
											'</div>';
                                if(commentVo.children != null && commentVo.children.length > 0) {
                                	html += '<div class="sub-comment-list">';
                                	$.each(commentVo.children,function (i, commentVo) {
										html += '<div class="sub-comment" id="'+commentVo.comment.id+'">' +
													'<div>' +
                                            			'<a href="/article/list/'+commentVo.user.id+'">'+commentVo.user.name+'</a>: ' +
														'<span>'+commentVo.comment.content+'</span>' +
													'</div>' +
													'<div class="tool">' +
														'<span>'+new Date(commentVo.comment.releaseDate).format()+'</span>' +
														'<a class="reply-input">回复</a>' +
														'<a class="report">举报</a>' +
													'</div>'+
												'</div>'
                                    })
                                    html += '</div>';
								}
                                html += '</div>';
                            })
                            html +='<div class="text-center" id="page"></div>';
						}
                        $("#comment-list").html(html);
                        total = data.total;
                        laypage.render({
                            elem: 'page',
                            count: data.total,
                            limit: pageSize,
                            curr: pageNum,
                            layout: ['count', 'prev', 'page', 'next', 'skip'],
                            jump: function (obj, first) {
                                if (!first) {
                                    page(obj.curr, pageSize)
                                }
                            }
                        });
					}
                }
			})
        }
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
    $(document).on("mouseover",".comment-wrap,.sub-comment",function() {
        $(this).find(".report")[0].style.display="block";
	})
    $(document).on("mouseout",".comment-wrap,.sub-comment",function() {
        $(this).find(".report")[0].style.display="none";
    })

$(document).on("click",".comment-input a:nth-child(2)",function() {
	if($(this).parent().parent().hasClass("reply")) {
		var comment = $(".reply .comment-input textarea").val();
		if($.trim(comment) == '') {
			alert("回复不能为空")
			return false;
		}
		reply = 0;
		$(".reply .comment-input textarea").val("");
		$this=$(this);
		var pid=$this.parent().parent().parent().parent().attr("id")
		comment = content+comment;
		content="";
		$.ajax({
			url:"/comment/comment",
			data:"aid="+$("#aid").val()+"&content="+comment+"&pid="+pid+"&cid="+cid,
			type:"post",
			success:function (resp) {
				if(resp.code==0) {
                    var data = resp.data;
                    var html=
                        '<div class="sub-comment" id="'+data.comment.id+'">' +
							'<div>' +
								'<a href="/article/list/'+data.user.id+'">'+data.user.name+'</a>：' +
								'<span>'+data.comment.content+'</span>' +
							'</div>' +
							'<div class="tool">' +
								'<span>'+new Date(data.comment.releaseDate).format()+'</span>' +
                        		'<a class="reply-input">回复</a>' +
								'<a class="report">举报</a>'+
							'</div>'
                        '</div>';
                    $this.parent().parent().parent().append(html);
                    $this.parent().parent().remove();
				}
            }
		})

	} else {
		var comment = $(".comment-input textarea").val();
		$(".comment-input textarea").val("");
		if($.trim(comment) == '') {
			alert("评论不能为空")
			return false;
		}
		reply = 0;
		$("#comment-input textarea").val("");
		$.ajax({
			url:"/comment/comment",
            data:"aid="+$("#aid").val()+"&content="+comment+"&cid="+cid,
			type:"post",
			success:function (resp) {
                if(resp.code==0) {
					var commentVo = resp.data;
					var html=
							'<div class="comment" id="'+commentVo.comment.id+'">' +
								'<div class="parent-comment">' +
									'<a class="headimg" href="/article/list/'+commentVo.user.id+'">' +
									'<img src="'+commentVo.user.headUrl+'">' +
									'</a>' +
									'<div class="info">' +
										'<a href="/article/list/'+commentVo.user.id+'">'+commentVo.user.name+'</a>' +
										'<span style="font-size: 12px;color: #969696;">'+(++total)+'楼 '+new Date(commentVo.comment.releaseDate).format()+'</span>' +
									'</div>'+
									'<div class="comment-wrap">' +
										'<p>'+commentVo.comment.content+'</p>' +
										'<div class="tool">' +
											'<a class="layui-icon" style="font-size: 12px;color: #969696;">&#xe6c6; 12人赞 </a>'+
											'<a class="reply-input">回复</a>' +
											'<a class="report">举报</a>'+
										'</div>'+
									'</div>'+
								'</div>'+
							'</div>';
                    $("#comment-list .comment:first").before(html);
				}
            },
			error:function (resp) {

            }
		})
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
var cid=0;
var content = '';
$(document).on("click",".reply-input",function() {
	if(reply == 1) {
		$(".reply").remove();
		reply = 0;
		return false;
	}
	reply++;
	var html = '<div class="comment-write reply">' +
					'<form class="comment-input">'+
						'<textarea class="form-control" placeholder="写下你的评论..."></textarea>' + 
						'<a>发送</a>' +
						'<a>取消</a>';
    if($(this).parent().parent().parent().hasClass("parent-comment")) {
    	html+="<span style='float: left;margin-top: 18px;'>@"+$(this).parent().parent().prev().children("a").html()+"</span>";
	}
    if($(this).parent().parent().hasClass("sub-comment")) {
    	html+="<span style='float: left;margin-top: 18px;'>@"+$(this).parent().prev().children("a").html()+"</span>";
	}
					html+='</form>' +
					'<div style="clear: both;"></div>' +
				'</div>';
	//评论回复
	if($(this).parent().parent().parent().hasClass("parent-comment")) {
		var sub = $(this).parent().parent().parent().next(".sub-comment-list");
		if (sub.length == 0) {
			html = '<div class="sub-comment-list reply">' + html +'</div>';
            $(this).parent().parent().parent().parent().append(html);
			
		} else {
			sub.append(html)
		}
		cid=$(this).parent().parent().parent().parent().attr("id");
		content = '<a href="'+$(this).parent().parent().prev().children("a").attr("href")+'">@'+$(this).parent().parent().prev().children("a").html()+"</a> ";
		console.log(content);
	}
	//子回复
	if($(this).parent().parent().hasClass("sub-comment")) {
		$(this).parent().parent().parent().append(html);
        cid=$(this).parent().parent().attr("id");
        content = '<a href="'+$(this).parent().prev().children("a").attr("href")+'">@'+$(this).parent().prev().children("a").html()+"</a> ";
        console.log(content);
	}
    $('.comment-input>textarea').focus()
})
})