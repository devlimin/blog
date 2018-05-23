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
            page(1,pageNum);
            return false;
        })
        page(1,pageNum);
        function page(pageNum,pageSize) {
            if(!isPositiveInteger($("input[name='tid']").val())){
                layer.msg('帖子id为正整数', {icon: 5,anim: 6});
                return false;
            }
            if(!isPositiveInteger($("input[name='uid']").val())){
                layer.msg('评论人id为正整数', {icon: 5,anim: 6});
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
                url:'/admin/forum/replyPage',
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
                        $.each(data.list,function (i, topic) {
                            html+='<tr id="'+topic.id+'">'
                                +'<td>'+topic.id+'</td>'
                                +'<td>'+topic.topicTitle+'</td>'
                                +'<td>'+topic.userId+'</td>'
                                +'<td>'+topic.userName+'</td>'
                                +'<td>'+new Date(topic.releaseDate).format()+'</td>';
                            if(topic.status==-1){
                                html+='<td>已删除</td>'
                            } else if(topic.status==0){
                                html+='<td>已发表</td>'
                            } else if(topic.status==1){
                                html+='<td>回收站</td>'
                            }
                            html+='<td>' +
                                    '<button class="layui-btn layui-btn-xs detail">详情</button>' +
                                    '<button class="layui-btn layui-btn-xs updatestate" >状态更改</button>' +
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
            var topicId = $(this).parent().parent().attr("id");
            $.ajax({
                url:'/admin/forum/replyInfo',
                type:'get',
                data:'id='+topicId,
                success:function (resp) {
                    if(resp.code==0){
                        var reply = resp.data;
                        var html='';
                        html+='<table class="layui-table">' +
                            '<tr>' +
                            '<td><label>帖子：</label><span>'+reply.topicTitle+'</span></td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td><label>评论人：</label><span>'+reply.userName+'</span></td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td>' +
                            '<label>发表时间：</label><span>'+new Date(reply.releaseDate).format()+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '</td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td><label>内容：</label><span>'+reply.content+'</span></td>' +
                            '</tr>'+
                            '</table>'
                        index = layer.open({
                            type: 1,
                            skin: 'layui-layer-molv', //加上边框
                            area: ['600px', '400px'], //宽高
                            title: "文章信息",
                            content:html
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

        var replyIdstatus;
        var index;
        var tr;
        $(document).on("click",".updatestate",function () {
            var replyId = $(this).parent().parent().attr("id");
            replyIdstatus = replyId;
            var status = $(this).parent().parent().children("td:nth-child(6)").text();
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
                '<option value="0">发表</option>\n' +
                '<option value="-1">删除</option>\n' +
                '</select>' +
                '<button class="layui-btn status-update-btn">提交</button>' +
                '</td>' +
                '</tr>' +
                '</table>'
            });
        })
        $(document).on("click",".status-update-btn",function () {
            var replyId = replyIdstatus;
            var status = $("#status-select").val();
            if(status==''){
                layer.msg("请选择一个状态", {icon: 5,anim: 6});
                return false;
            }
            var data = 'replyId='+replyId+"&status="+status
            $.ajax({
                url:'/admin/forum/updateReplyStatus',
                data:data,
                type:'post',
                success:function (resp) {
                    if (resp.code==0) {
                        layer.close(index);
                        var text=''
                        if (status==0) {
                            text='已发表'
                        } else if (status==-1) {
                            text='已删除'
                        }
                        tr.children("td:nth-child(6)").text(text);
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