$(function () {
    layui.use(['layer','laypage','element'], function(){
        var laypage = layui.laypage;
        var element = layui.element;
        page(1,5);
        function page(pageNum,pageSize) {
            var data = "pageNum="+pageNum+"&pageSize="+pageSize
            $.ajax({
                url:'/admin/article/articlePage',
                type:"get",
                data:data,
                success:function (resp) {
                    if (resp.code==0) {
                        var html='';
                        var data = resp.data;
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>暂无数据</div>";
                            $("#table").html(html);
                            return false;
                        }
                        $.each(data.list,function (i, articleVo) {
                            html+='<tr>'
                                +'<td>'+articleVo.article.id+'</td>'
                                +'<td>'+articleVo.sysCategory.name+'</td>'
                                +'<td>'+articleVo.article.title+'</td>'
                                +'<td>'+articleVo.user.name+'</td>'
                                +'<td>'+new Date(articleVo.article.releaseDate).format()+'</td>'
                                +'<td>'+articleVo.article.status+'</td>'
                                +'<td><button>操作</button></td>'
                                +'</tr>'
                        })
                        $("#table tr:not(:first)").remove();
                        $("#table").append(html);
                        if (data.total>pageSize) {
                            laypage.render({
                                elem: 'page',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page( obj.curr, pageSize)
                                    }
                                }
                            });
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                    return false;
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                    return false;
                }
            })
        }
    });
    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})