$(function () {
	var total=0;
	layui.use(['laypage','layer'], function() {
        var laypage = layui.laypage;
        var layer = layui.layer;
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
                            html = "<div class='comment'></div>";
                        } else {
                        	var isComment = $("#isComment").val();
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
														'<a class="layui-icon" style="font-size: 12px;color: #969696;">&#xe6c6; 12人赞 </a>';
                                					if(isComment=="true"){
                                                        html+='<a class="reply-input">回复</a>'
													}
														html+='<a class="report">举报</a>'+
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
														'<span>'+new Date(commentVo.comment.releaseDate).format()+'</span>';
													if(isComment=="true"){
														html+='<a class="reply-input">回复</a>'
													}
														html+='<a class="report">举报</a>' +
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
                        if(data.total>pageSize){
                            laypage.render({
                                elem: 'page',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page(obj.curr, pageSize)
                                        $('html').animate({scrollTop: $("#comment").offset().top}, 100)
                                    }
                                }
                            });

						}
					}
                }
			})
        }
        var index;
        $(document).on("click","#mail",function () {
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            index = layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: ['420px', '300px'], //宽高
                title: "发私信",
                content: ' <div style="margin: 10px">' +
                '<div class="layui-form-item layui-form-text">\n' +
                '    <label class="layui-form-label" style="padding-left: 0px;width: 50px;">内容</label>\n' +
                '    <div class="layui-input-block" style="margin-left: 60px;">\n' +
                '      <textarea id="msg_content" style="height: 150px;" placeholder="请输入内容" class="layui-textarea"></textarea>\n' +
                '    </div>\n' +
                '  </div>' +
                '  <div class="layui-form-item">\n' +
                '    <div class="layui-input-block" style="margin-left: 60px;">\n' +
                '      <button id="send" class="layui-btn">发送</button>\n' +
                '      <button id="cancle" class="layui-btn layui-btn-primary">取消</button>\n' +
                '    </div>\n' +
                '  </div>' +
                '</div> '
            })
        })
        $(document).on("click", "#send", function () {
            var toUserId = $("#toUserId").val();
            var content = $.trim($("#msg_content").val());
            if(content=="") {
                layer.msg("不能为空", {icon: 5,anim: 6});
                return false;
            }
            var data = "toUserId=" + toUserId + "&content=" + content;
            $.ajax({
                url: "/message/man/add",
                data: data,
                type: "post",
                success: function (resp) {
                    if (resp.code == 0) {
                        var messageVo = resp.data;
                        var html = '<div class="my-msg">' +
                            '            <div class="msg" >' +
                            '                    <p class="content">' + messageVo.message.content + '</p>' +
                            '                    <span style="float: right;">' + new Date(messageVo.message.releaseDate).format() + '</span>' +
                            '                </div>' +
                            '                <a href="/article/list/' + messageVo.user.id + '">' +
                            '                    <img class="headImg" src="' + messageVo.user.headUrl + '"/></a>' +
                            '            </div>';
                        $("#messages").prepend(html);
                        layer.close(index);
                    } else {
                        layer.msg(resp.msg, {icon: 5, anim: 6});
                    }
                },
                error: function (resp) {
                    layer.msg(resp.msg, {icon: 5, anim: 6});
                }
            })
            layer.close(index);
        })
        $(document).on("click", "#cancle", function () {
            layer.close(index);
        })

        $(document).on("click","#follow",function () {
            var entityId = $("#toUserId").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            var url=null;
            if($this.hasClass("unfollow")){
                url="/follow/man/followuser"
            } else if ($this.hasClass("follow")){
                url="/follow/man/unfollowuser"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success: function (resp) {
                    if(resp.code == 0) {
                        if($this.hasClass("unfollow")){
                            $this.removeClass("unfollow");
                            $this.removeClass("layui-btn-primary");
                            $this.addClass("follow");
                            $this.text("已关注");
                        } else if ($this.hasClass("follow")){
                            $this.removeClass("follow");
                            $this.addClass("unfollow");
                            $this.addClass("layui-btn-primary");
                            $this.text("关注");
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
        $("#black").click(function () {
            var entityId = $("#toUserId").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            var url = null;
            if($this.hasClass("white")){
                url="/follow/man/black"
            } else if($this.hasClass("black")){
                url="/follow/man/white"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success:function (resp) {
                    if (resp.code==0){
                        if($this.hasClass("black")) {
                            $this.removeClass("black");
                            $this.addClass("white")
                            $this.addClass("layui-btn-primary")
                            $this.text("黑名单")
                            $this.before('<button id="follow" class="unfollow layui-btn layui-btn-primary layui-btn-xs">关注</button>' +
                                         '<button class="layui-btn layui-btn-xs" id="mail">私信</button>')
                            $("#title>span").after('<button style="margin-top: 25px;" id="mark"\n' +
                                'class="unMark layui-btn layui-btn-primary pull-right">收藏</button>')
                        } else if($this.hasClass("white")) {
                            $this.removeClass("white");
                            $this.addClass("black")
                            $this.removeClass("layui-btn-primary")
                            $this.text("已拉黑")
                            $("#follow").remove();
                            $("#mail").remove();
                            $("#mark").remove();
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
        $(document).on("click","#mark",function () {
            var entityId = $("#aid").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            if($this.hasClass("unMark")){
                var url="/follow/man/markArticle"
            } else if ($this.hasClass("Mark")){
                var url="/follow/man/unmarkArticle"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success: function (resp) {
                    if(resp.code == 0) {
                        if($this.hasClass("unMark")){
                            $this.removeClass("unMark");
                            $this.removeClass("layui-btn-primary");
                            $this.addClass("Mark");
                            $this.text("已收藏");
                        } else if ($this.hasClass("Mark")){
                            $this.removeClass("Mark");
                            $this.addClass("unMark");
                            $this.addClass("layui-btn-primary");
                            $this.text("收藏");
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
        

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
    var loginUserId= $("#loginUserId").val();
    if (loginUserId=="") {
        window.location.href="/account?action=login&next="+window.location.pathname;
        return false;
    }
	if($(this).parent().parent().hasClass("reply")) {
		var comment = $(".reply .comment-input textarea").val();
		if($.trim(comment) == '') {
            layer.msg("回复不能为空", {icon: 5,anim: 6});
			return false;
		}
		reply = 0;
		$(".reply .comment-input textarea").val("");
		$this=$(this);
		var pid=$this.parent().parent().parent().parent().attr("id")
		comment = content+comment;
		var data="aid="+$("#aid").val()+"&content="+comment+"&pid="+pid+"&cid="+cid;
		content="";
		cid=-1;
		$.ajax({
			url:"/comment/man/comment",
			data:data,
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
				} else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
				}
            },error:function (resp) {
                layer.msg(resp.msg, {icon: 5,anim: 6});
            }
		})

	}
	else {
		var comment = $(".comment-input textarea").val();
		$(".comment-input textarea").val("");
		if($.trim(comment) == '') {
            layer.msg("回复不能为空", {icon: 5,anim: 6});
			return false;
		}
		reply = 0;
		$("#comment-input textarea").val("");
		var data ="aid="+$("#aid").val()+"&content="+comment;
		cid=-1
		$.ajax({
			url:"/comment/man/comment",
            data:data,
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
				} else{
                    layer.msg(resp.msg, {icon: 5,anim: 6});
				}
            },
			error:function (resp) {
                layer.msg(resp.msg, {icon: 5,anim: 6});
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
var cid=-1;
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
			html = '<div class="sub-comment-list">' + html +'</div>';
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
    $('.comment-input').focus()
})
})