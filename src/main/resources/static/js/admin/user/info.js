$(function () {
    layui.use(['layer','laypage','element'], function(){
        var laypage = layui.laypage;
        var element = layui.element;
        page(1,5);
        function page(pageNum,pageSize) {
            var data = "pageNum="+pageNum+"&pageSize="+pageSize
            $.ajax({
                url:'/admin/user/page',
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
                        $.each(data.list,function (i, user) {
                            html+='<tr>'
                                +'<td>'+user.id+'</td>'
                                +'<td>'+user.email+'</td>'
                                +'<td>'+user.name+'</td>'
                                +'<td>'+user.motto+'</td>'
                                +'<td>'+user.birth+'</td>'
                                +'<td>'+user.status+'</td>'
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
})