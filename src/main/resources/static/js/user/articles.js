$(function () {
layui.use(['laypage','element'], function() {
    var laypage = layui.laypage;
    page(1,10);
    function page(pageNum, pageSize) {
        var uid = $("#uid").val();
        var cid = $("#cid").val();
        var url = "/article/page/" + uid;
        if (cid != "") {
            url += "/" + cid;
        }
        $.ajax({
            url: url,
            data: "pageNum=" + pageNum + "&pageSize=" + pageSize,
            type: "get",
            success: function (resp) {
                if (resp.code == 0) {
                    var data = resp.data;
                    var html = ""
                    if(data == null || data.list == null || data.list.length == 0) {
                        html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                    } else {
                        $.each(data.list, function (i, article) {
                            html += '<div class="article">' +
                                '<a href="/article/detail/' + article.id + '" class="title">' + article.title + '</a>' +
                                '<p class="detail">' + article.content + '...' +
                                '</p>' +
                                '<div class="other">' +
                                '<span>' + new Date(article.releaseDate).format() + '</span>' +
                                '<span class="layui-icon right">&#xe705 ' + article.readNum + '</span>' +
                                '<span class="layui-icon right">&#xe6b2 ' + article.commentNum + '</span>' +
                                '</div>' +
                                '</div>'
                        })
                        html +='<div class="text-center" id="page"></div>';
					}
                    $("#article").html(html);
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
                } else {
                    html="<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>"+resp.msg+"</div>";
                    $("#article").html(html);
				}
            },
            error: function (resp) {
            	html="<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>"+resp.msg+"</div>";
                $("#article").html(html);
            }
        })
    }
})

        Date.prototype.format = function () {
            return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
        }
})
