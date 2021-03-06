$(function () {
    layui.use(['layer','laypage','laydate','element','form'], function(){
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
            if(!isPositiveInteger($("input[name='aid']").val())){
                layer.msg('文章id为正整数', {icon: 5,anim: 6});
                return false;
            }
            if(!isPositiveInteger($("input[name='uid']").val())){
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
            $.ajax({
                url:'/admin/article/articlePage',
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
                        $.each(data.list,function (i, article) {
                            html+='<tr id="'+article.id+'">'
                                +'<td>'+article.id+'</td>'
                                +'<td>'+article.title+'</td>'
                                +'<td>'+article.userId+'</td>'
                                +'<td>'+new Date(article.releaseDate).format()+'</td>';
                            if(article.status==-1){
                                html+='<td>已删除</td>'
                            } else if(article.status==0){
                                html+='<td>已发表</td>'
                            } else if(article.status==1){
                                html+='<td>草稿</td>'
                            } else if(article.status==2){
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
    });

    $(document).on("click",".detail",function () {
        var articleId = $(this).parent().parent().attr("id");
        $.ajax({
            url:'/admin/article/articleInfo',
            type:'get',
            data:'id='+articleId,
            success:function (resp) {
                if(resp.code==0){
                    var articleVo = resp.data;
                    var html='';
                    html+='<table class="layui-table">' +
                        '<tr>' +
                        '<td><label>标题：</label><span>'+articleVo.article.title+'</span></td>' +
                        '</tr>'+
                        '<tr>' +
                        '<td><label>系统分类：</label><span>'+articleVo.sysCategory.name+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '<label>个人分类：</label>';
                            if(articleVo.categories!=null&&articleVo.categories.length>0){
                                $.each(articleVo.categories,function (i,category) {
                                    html+='<span>'+category.name+'</span>';
                                });
                            }
                        html+='</td></tr>'+
                        '<tr>' +
                        '<td>' +
                        '<label>作者：</label><span>'+articleVo.user.name+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '<label>发表时间：</label><span>'+new Date(articleVo.article.releaseDate).format()+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '<label>阅读数：</label><span>'+articleVo.article.readNum+'</span>&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '<label>评论数：</label><span>'+articleVo.article.commentNum+'</span>' +
                        '</td>' +
                        '</tr>'+
                        '<tr>' +
                        '<td><label>内容：</label><span>'+articleVo.article.content+'</span></td>' +
                        '</tr>'+
                        '</table>'
                    index = layer.open({
                        type: 1,
                        skin: 'layui-layer-molv', //加上边框
                        area: ['800px', '600px'], //宽高
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

    var articleIdstatus;
    var index;
    var tr;
    $(document).on("click",".updatestate",function () {
        var articleId = $(this).parent().parent().attr("id");
        articleIdstatus = articleId;
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
            '<option value="0">发表</option>\n' +
            '<option value="1">草稿</option>\n' +
            '<option value="2">回收站</option>\n' +
            '<option value="-1">删除</option>\n' +
            '</select>' +
            '<button class="layui-btn status-update-btn">提交</button>' +
            '</td>' +
            '</tr>' +
            '</table>'
        });
    })
    $(document).on("click",".status-update-btn",function () {
        var articleId = articleIdstatus;
        var status = $("#status-select").val();
        if(status==''){
            layer.msg("请选择一个状态", {icon: 5,anim: 6});
            return false;
        }
        var data = 'articleId='+articleId+"&status="+status
        $.ajax({
            url:'/admin/article/updateArticleStatus',
            data:data,
            type:'post',
            success:function (resp) {
                if (resp.code==0) {
                    layer.close(index);
                    var text=''
                    if (status==0) {
                        text='已发表'
                    } else if (status==1) {
                        text='草稿'
                    } else if (status==2) {
                        text='回收站'
                    } else if (status==-1) {
                        text='已删除'
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

    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})