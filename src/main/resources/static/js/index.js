$(function () {
layui.use('laypage', function(){
	var laypage = layui.laypage;
    function page(pageNum,pageSize) {
        var data = "pageNum="+pageNum+"&pageSize="+pageSize;
        var sysCateId = $("#sysCateId").val();
        if (sysCateId!="") {
            data += "&sysCateId="+sysCateId;
        }
		$.ajax({
			url:"/index/page",
			data:data,
			type:"get",
			success:function (resp) {
				if(resp.code==0) {
					var data = resp.data;
					var html = "";
                    if(data == null || data.list == null || data.list.length == 0) {
                        html = "<div style='text-align: center;padding-top: 40px;padding-bottom: 30px;background-color: white;'>该分类暂无数据</div>";
                        $("#articles").html(html);
                        return false;
                    }
					$.each(data.list, function (i,articleVo) {
						html += '<div class="article">' +
									'<div class="detail">' +
										'<div>' +
											'<a href="/article/detail/'+articleVo.article.id+'">'+articleVo.article.title+'</a>' +
											'<a href="/index?sysCateId='+articleVo.sysCategory.id+'">'+articleVo.sysCategory.name+'</a>' +
										'</div>' +
										'<p>'+ articleVo.article.content +'</p>' +
									'</div>' +
									'<div class="other">' +
										'<a href="/article/list/'+articleVo.user.id+'">' +
										'<img src="'+articleVo.user.headUrl+'" class="img-circle"> ' +
										'<span>'+articleVo.user.name+'</span>' +'</a>' +
										'<span>'+new Date(articleVo.article.releaseDate).format()+'</span>' +
										'<span class="layui-icon">&#xe6b2 '+articleVo.article.commentNum+'</span>' +
										'<span class="layui-icon">&#xe705 '+articleVo.article.readNum+'</span>' +
									'</div>' +
                            	'</div>';
                    })
                    html +='<div class="text-center" id="page"></div>';
                    $("#articles").html(html);
                    if (data.total>pageSize) {
                        laypage.render({
                            elem: 'page',
                            count: data.total,
                            limit: pageSize,
                            curr: pageNum,
                            layout: ['count', 'prev', 'page', 'next', 'skip'],
                            jump: function (obj, first) {
                                if (!first) {
                                    page(obj.curr, pageSize)
                                    $('html').animate({scrollTop: 0}, 100)
                                }
                            }
                        });
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
    page(1,10);
});
$(".list-group a").hover(
	function(){
		$(this).css({"background-color":"red", "color":"white"});
	}, 
	function(){
		$(this).css({"background-color":"white", "color":"black"});
	}
);
	

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
    Date.prototype.format = function () {
        return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
});