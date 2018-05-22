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
            if(!isPositiveInteger($("input[name='id']").val())){
                layer.msg('作者id为正整数', {icon: 5,anim: 6});
                return false;
            }
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
                        $("#nocontent").remove();
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div id='nocontent' style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>暂无数据</div>";
                            $("#table").append(html);
                            $("#page").remove();
                            $("#thead").css({"display":"none"});
                            $("#table tr:not(:first)").remove();
                            return false;
                        }
                        $("#thead").css({"display":"table-row"});
                        $.each(data.list,function (i, user) {
                            html+='<tr id="'+user.id+'">'
                                +'<td>'+user.id+'</td>'
                                +'<td>'+user.email+'</td>'
                                +'<td>'+user.name+'</td>'
                                +'<td>'+new Date(user.birth).format()+'</td>';
                            if(user.state==0) {
                                html+='<td>未激活</td>'
                            } else if(user.state == 1){
                                html+='<td>已激活</td>'
                            } else if(user.state == 2) {
                                html+='<td>禁用</td>'
                            }
                            html+='<td>' +
                                        '<button class="layui-btn layui-btn-xs detail">详情</button>' +
                                        '<button class="layui-btn layui-btn-xs updatestate" >状态更改</button>' +
                                        '<button class="layui-btn layui-btn-xs updatepass" >重置密码</button>' +
                                    '</td>'
                                +'</tr>'
                        })
                        $("#table tr:not(:first)").remove();
                        $("#table").append(html);
                        $("#page").remove();
                        if (data.total>pageSize) {
                            $("#table").after('<div id="page" class="text-center"></div>');
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

        $(document).on("click",".detail",function () {
            var userId = $(this).parent().parent().attr("id");
            $.ajax({
                url:'/user/info',
                type:'get',
                data:'userId='+userId,
                success:function (resp) {
                    if(resp.code==0){
                        var data = resp.data;
                        index = layer.open({
                            type: 1,
                            skin: 'layui-layer-molv', //加上边框
                            area: ['600px', '450px'], //宽高
                            title: "用户信息",
                            content:
                                    '<table class="layui-table">' +
                                        '<tr>' +
                                            '<td>邮箱</td>' +
                                            '<td>'+data.email+'</td>' +
                                            '<td>用户名</td>' +
                                            '<td>'+data.name+'</td>' +
                                        '</tr>' +
                                        '<tr>' +
                                            '<td>头像</td>' +
                                            '<td><img src="'+data.headUrl+'"/></td>' +
                                            '<td>入驻时间</td>' +
                                            '<td>'+new Date(data.birth).format()+'</td>' +
                                        '</tr>' +
                                        '<tr>' +
                                            '<td>座右铭</td>' +
                                            '<td colspan="3">'+data.motto+'</td>' +
                                        '</tr>' +
                                    '</table>'
                        })
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

        })
        var userIdstatus;
        var index;
        var tr;
        $(document).on("click",".updatestate",function () {
            var userId = $(this).parent().parent().attr("id");
            userIdstatus = userId;
            var status = $(this).parent().parent().children("td:nth-child(5)").text();
            tr = $(this).parent().parent();
            index = layer.open({
                type: 1,
                skin: 'layui-layer-molv', //加上边框
                area: ['500px', '250px'], //宽高
                title: "状态更改",
                content:
                '<table class="layui-table">' +
                '<tr>' +
                '<td><label>当前状态</label>：'+status+'</td>' +
                '</tr>' +
                '<tr>' +
                '<td><label>可选状态</label>：' +
                '<select id="status-select" class="form-control" style="width:300px;display: inline-block;" name="status">' +
                '<option value="">请选择</option>\n' +
                '<option value="0">未激活</option>\n' +
                '<option value="1">已激活</option>\n' +
                '<option value="2">禁用</option>\n' +
                '</select>' +
                '<button class="layui-btn status-update-btn">提交</button>' +
                '</td>' +
                '</tr>' +
                '</table>'
            });
        })
        $(document).on("click",".status-update-btn",function () {
            var userId = userIdstatus;
            var status = $("#status-select").val();
            if(status==''){
                layer.msg("请选择一个状态", {icon: 5,anim: 6});
                return false;
            }
            var data = 'userId='+userId+"&status="+status
            $.ajax({
                url:'/admin/user/updateStatus',
                data:data,
                type:'post',
                success:function (resp) {
                    if (resp.code==0) {
                        layer.close(index);
                        var text=''
                        if (status==0) {
                            text='未激活'
                        } else if (status==1) {
                            text='已激活'
                        } else if (status==2) {
                            text='禁用'
                        }
                        tr.children("td:nth-child(5)").text(text);
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
        })
        $(document).on("click",".updatepsss",function () {
            var userId = $(this).parent().parent().attr("id");
            $.ajax({
                url:'',
                data:'',
                type:'post',
                success:function (resp) {
                    if (resp.code==0) {

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
        })
    });
})