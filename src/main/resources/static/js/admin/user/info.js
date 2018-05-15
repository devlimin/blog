$(function () {
    layui.use(['layer','laypage','element','laydate','form'], function(){
        var layer = layui.layer;
        var laypage = layui.laypage;
        var element = layui.element;
        var laydate = layui.laydate;
        var form = layui.form;

        //执行一个laydate实例
        //同时绑定多个
        lay('.time-item').each(function(){
            laydate.render({
                elem: this,
                type: 'datetime',
                max:0,
                trigger: 'click'
            });
        });

        $("#search_btn").click(function () {
            page(1,5);
            return false;
        })
        page(1,5);
        function page(pageNum,pageSize) {
            var data = $("form").serialize();
            var beginTime = "";
            console.log($("#beginTime").val());
            if($("#beginTime").val()!="") {
                beginTime = new Date($("#beginTime").val()).getTime();
            }
            var endTime = ""
            if($("#endTime").val()!="") {
                endTime = new Date($("#endTime").val()).getTime();
            }
            data += "&beginTime="+beginTime+"&endTime="+endTime+"&pageNum="+pageNum+"&pageSize="+pageSize
            console.log(data);
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
                                +'<td>'+new Date(user.birth).format()+'</td>';
                            if(user.state==0) {
                                html+='<td>未激活</td>'
                            } else if(user.state == 1){
                                html+='<td>已激活</td>'
                            } else {
                                html+='<td>未知</td>'
                            }
                            html+='<td>' +
                                        '<button class="layui-btn layui-btn-xs">详情</button>' +
                                    '</td>'
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