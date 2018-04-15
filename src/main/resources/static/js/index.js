$(function () {
layui.use('laypage', function(){
	var laypage = layui.laypage;
	page(1,10);
    function page(pageNum,pageSize) {
		$.ajax({
			url:"/index/page",
			data:"pageNum="+pageNum+"&pageSize="+pageSize,
			type:"get",
			success:function (resp) {
				if(resp.code==0) {
					var data = resp.data;
					var html = "";
					$.each(data.list, function (i,article) {
						html += '<div class="article">' +
                            '<div class="detail">' +
                            '<div>' +
                            '<a href="/article/detail/'+article.id+'">'+article.title+'</a>' +
                            '<a href="">推荐</a>' +
                            '</div>' +
                            '<p>' + article.content +
                            '</p>' +
                            '</div>' +
                            '<div class="other">' +
                            '<a href="#">' +
                            '<img src="https://images.nowcoder.net/head/349m.png@0e_100w_100h_0c_1i_1o_90Q_1x.png" class="img-circle">' +
                            '<span>东方不败东方不败东方不败东方不败东方不败</span>' +
                            '</a>' +
                            '<span>'+new Date(article.releaseDate).format()+'</span>' +
                            '<span class="layui-icon">&#xe6b2 '+article.commentNum+'</span>' +
                            '<span class="layui-icon">&#xe705 '+article.readNum+'</span>' +
                            '</div>' +
                            '</div>';
                    })
                    html +='<div class="text-center" id="page"></div>';
                    $("#articles").html(html);
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
                    $('html').animate({ scrollTop: 0 }, 100)
				} else{

				}
            },
			error:function (resp) {

            }
		})
    }

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


$(document).on("click","#search-btn",function() {
	console.log($("#search-input").val());
})
    Date.prototype.format = function () {
        return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
});