$(function () {
    layui.use(['laypage','layer','element'], function(){
        var laypage = layui.laypage;
        var layer = layui.layer;
        var element = layui.element;

        page(1,10);
        $("#search_area>button").click(function () {
            var keyword =  $.trim($("#search_input").val());
            if(keyword == "") {
                layer.msg("请输入关键字", {icon: 5,anim: 6});
                return false;
            }
            $("#keywords").val(keyword);
            page(1,10);
        })
        $('#search_input').on('keypress', function(event) {
            if (event.keyCode === 13) {
                $('#search_area>button').trigger('click');
            }
        });
        element.on('tab(test1)', function(data){
            layid = this.getAttribute('lay-id');
            if (layid == 0){
                $("#type").val("article")
                page(1,10);
            }
            if (layid == 1){
                $("#type").val("topic")
                page(1,10);
            }
        })
        function page(pageNum,pageSize) {
            var type = $.trim($("#type").val());
            var keywords = $.trim($("#keywords").val());
            if(type=="article"){
                var data = "keywords="+keywords+"&pageNum="+pageNum+"&pageSize="+pageSize;
                $.ajax({
                    url:'/searcharticle',
                    type:"get",
                    data:data,
                    success:function (resp) {
                        if (resp.code==0) {
                            var html="";
                            var data = resp.data;
                            if(data == null || data.list == null || data.list.length == 0) {
                                html = "<div style='text-align: center;padding-top: 40px;padding-bottom: 30px;background-color: white;'>该分类暂无数据</div>";
                                $(".layui-tab-item:first-child").html(html);
                                return false;
                            }
                            $.each(data.list,function (index, articleVo) {
                                html+='<div class="item">' +
                                    '<a href="/article/detail/'+articleVo.article.id+'" class="title">'+articleVo.article.title+'</a>' +
                                    '<div style="margin-bottom: 2px;">' +
                                    '<span>作者：<a href="/article/list/'+articleVo.user.id+'">'+articleVo.user.name+'</a></span>' +
                                    '<span>日期：'+new Date(articleVo.article.releaseDate).format()+'</span>' +
                                    '<span>浏览：'+articleVo.article.readNum+'</span>' +
                                    '<span>评论：'+articleVo.article.commentNum+'</span>' +
                                    '</div>' +
                                    '<p>'+articleVo.article.content+'</p>' +
                                    '</div>'
                            })
                            html +='<div class="text-center" id="articlePage"></div>';
                            $(".layui-tab-item:first-child").html(html);
                            if (data.total>pageSize) {
                                laypage.render({
                                    elem: 'articlePage',
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
            else if(type=="topic"){
                var data = "keywords="+keywords+"&pageNum="+pageNum+"&pageSize="+pageSize;
                $.ajax({
                    url:'/searchTopic',
                    type:"get",
                    data:data,
                    success:function (resp) {
                        if (resp.code==0) {
                            var html="";
                            var data = resp.data;
                            if(data == null || data.list == null || data.list.length == 0) {
                                html = "<div style='text-align: center;padding-top: 40px;padding-bottom: 30px;background-color: white;'>该分类暂无数据</div>";
                                $(".layui-tab-item:first-child").html(html);
                                return false;
                            }
                            $.each(data.list,function (index, topic) {
                                html+='<div class="item">' +
                                    '<a href="/forum/topic/'+topic.id+'" class="title">'+topic.title+'</a>' +
                                    '<div style="margin-bottom: 2px;">' +
                                    '<span>作者：<a href="/article/list/'+topic.userId+'">'+topic.userName+'</a></span>' +
                                    '<span>日期：'+new Date(topic.releaseDate).format()+'</span>' +
                                    '<span>浏览：'+topic.readNum+'</span>' +
                                    '<span>评论：'+topic.commentNum+'</span>' +
                                    '</div>' +
                                    '<p>'+topic.content+'</p>' +
                                    '</div>'
                            })
                            html +='<div class="text-center" id="topicPage"></div>';
                            $(".layui-tab-item:nth-child(2)").html(html);
                            if (data.total>pageSize) {
                                laypage.render({
                                    elem: 'topicPage',
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
        }
    })
})
