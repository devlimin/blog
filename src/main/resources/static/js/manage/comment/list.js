$(function() {
	var pageSize=10;
layui.use(['laypage','element'], function() {
	var laypage = layui.laypage;
	var element = layui.element;
    page(0,1,pageSize);
    element.on('tab(test1)', function(data){
        layid = this.getAttribute('lay-id');
        location.hash = 'test1='+ layid;
        if (layid == 0){
            page(0,1,pageSize);
        }
        if (layid == 1){
            page(1,1,pageSize);
        }
    })
    function page(status,pageNum,pageSize) {
        $.ajax({
            url: "/comment/man/page",
            data: "status=" + status + "&pageNum=" + pageNum + "&pageSize=" + pageSize,
            type: "get",
            success: function (resp) {
                if(resp.code == 0) {
                    var data = resp.data;
                    var html = "";
                    $.each(data.list, function (i, commentVo) {
						html += '<div class="comment" id="'+commentVo.comment.id+'">' +
									'<div class="title">';
						if(status==0){
							html+='<a href="/article/list/'+commentVo.user.id+'"><img src="'+commentVo.user.headUrl+'" class="img-circle"></a>' +
                                '<span><a href="/article/list/\'+commentVo.user.id+\'">'+commentVo.user.name+'</a></span>' +
                                '<span>'+new Date(commentVo.comment.releaseDate).format()+'</span>' +
                                '回复了你的文章: ' +
                                '<span><a href="/article/detail/'+commentVo.article.id+'">'+commentVo.article.title+'</a></span>';
						} else if(status==1){
                            html+='<span>'+new Date(commentVo.comment.releaseDate).format()+'</span>' +
                                '回复了' +
                                '<span><a href="/article/list/\'+commentVo.user.id+\'">'+commentVo.user.name+'</a></span>的文章：' +
                                '<span><a href="/article/detail/'+commentVo.article.id+'">'+commentVo.article.title+'</a></span>';
						}
										html+='<span class="action">';
											if(status==0) {
												html+='<a class="comment-btn">快速回复</a> | '+
                                                    '<a class="">举报</a> | ';
											}
											html+= '<a class="comment-del" style="color: red">删除</a>' +
										'</span>' +
									'</div>' +
									'<div class="detail">' +commentVo.comment.content + '</div>' +
								'</div>'
                    })
                    if (status == 0) {
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                            $(".layui-tab-item:first-child").html(html);
                            return false;
                        }
                        html += '<div id="in" class="text-center"></div>';
                        $(".layui-tab-item:first-child").html(html);
                        if (data.total>pageSize) {
                            laypage.render({
                                elem: 'in',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page(0, obj.curr, pageSize)
                                        $('html').animate({scrollTop: 0}, 100)
                                    }
                                }
                            });
                        }
                    } else if(status==1){
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                            $(".layui-tab-item:nth-child(2)").html(html);
                            return false;
                        }
                        html += '<div id="out" class="text-center"></div>';
                        $(".layui-tab-item:nth-child(2)").html(html);
                        if (data.total>pageSize) {
                            laypage.render({
                                elem: 'out',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page(1, obj.curr, pageSize)
                                        $('html').animate({scrollTop: 0}, 100)
                                    }
                                }
                            });
                        }
					}
                } else{
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
            }
        })
    }
});
    $(document).on("mouseover",".comment",function () {
        $(this).find(".action")[0].style.display="block";
    })
    $(document).on("mouseout",".comment",function () {
        $(this).find(".action")[0].style.display="none";
    })

    Date.prototype.format = function () {
        return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
$(document).on("click",".comment-btn",function() {
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
	var cid = $(this).parent().parent().parent().attr("id");
	$this=$(this);
	$.ajax({
        url:"/comment/man/quickcomment",
        data:"cid="+cid+"&content="+reply,
        type:"post",
        success:function (resp) {
            if(resp.code==0) {
                $this.parent().remove();
            }
        }
    })

})
$(document).on("click",".comment-del",function() {
	var cid = $(this).parent().parent().parent().attr("id");
	$this=$(this)
	$.ajax({
		url:"/comment/man/del",
        data:"id="+cid,
        type:"post",
        success:function (resp) {
            if(resp.code==0){
                $this.parent().parent().parent().remove();
            }
        }
	})
});
})