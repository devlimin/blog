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
                url:'/admin/forum/topicPage',
                type:"get",
                data:data,
                success:function (resp) {
                    if (resp.code==0) {
                        var html='';
                        var data = resp.data;
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div id='nocontent' style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>暂无数据</div>";
                            $("#table").append(html);
                            $("#page").remove();
                            $("#thead").css({"display":"none"});
                            $("#table tr:not(:first)").remove();
                            return false;
                        }
                        $("#nocontent").remove();
                        $("#thead").css({"display":"table-row"});
                        $.each(data.list,function (i, topic) {
                            html+='<tr id="'+topic.id+'">'
                                +'<td>'+topic.id+'</td>'
                                +'<td>'+topic.themeName+'</td>'
                                +'<td>'+topic.title+'</td>'
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
                url:'/admin/forum/topicInfo',
                type:'get',
                data:'id='+topicId,
                success:function (resp) {
                    if(resp.code==0){
                        var topic = resp.data;
                        var html='';
                        html+='<table class="layui-table">' +
                            '<tr>' +
                            '<td><label>标题：</label><span>'+topic.title+'</span></td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td><label>系统分类：</label><span>'+topic.themeName+'</span></td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td>' +
                            '<label>作者：</label><span>'+topic.userName+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<label>发表时间：</label><span>'+new Date(topic.releaseDate).format()+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<label>阅读数：</label><span>'+topic.readNum+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<label>评论数：</label><span>'+topic.commentNum+'</span>' +
                            '</td>' +
                            '</tr>'+
                            '<tr>' +
                            '<td><label>内容：</label><span>'+topic.content+'</span></td>' +
                            '</tr>'+
                            '</table>'
                        index = layer.open({
                            type: 1,
                            skin: 'layui-layer-molv', //加上边框
                            area: ['600px', '450px'], //宽高
                            title: "帖子信息",
                            content: html
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
    });
})