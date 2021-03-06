$(function () {
    layui.use(['laypage','element','layer'], function(){
        var laypage = layui.laypage;
        var element = layui.element;
        var layer = layui.layer;
        page(0,1,10);
        //监听Tab切换，以改变地址hash值
        element.on('tab(test1)', function(data){
            layid = this.getAttribute('lay-id');
            // location.hash = 'test1='+ layid;
            if (layid == 0){
                page(0,1,10);
            }
            if (layid == 1){
                page(1,1,10);
            }
        })
        function page(status,pageNum,pageSize) {
            $.ajax({
                url: "/forum/man/page",
                data: "status="+status+"&pageNum="+pageNum+"&pageSize="+pageSize,
                type: "get",
                success: function (resp) {
                    if(resp.code == 0) {
                        var data = resp.data;
                        var html = "";
                        $.each(data.list, function (i, topic) {
                            html += '<div class="article" id="'+topic.id+'">';
                            if(status == 0) {
                                html += '<div class="title"><a href="/forum/topic/'+topic.id+'">'+topic.title+'</a></div>';
                            } else {
                                html += '<div class="title">'+topic.title+'</div>';
                            }
                            html+='<div class="info">' +
                                '<div class="left">' +
                                '<span>'+new Date(topic.releaseDate).format()+'</span>' +
                                '<span class="layui-icon">&#xe705 '+topic.readNum+'</span>' +
                                '<span class="layui-icon">&#xe6b2 '+topic.commentNum+'</span>' +
                                '</div>' +
                                '<div class="right">'+
                                '<a href="/forum/man/post/'+topic.id+'">编辑</a>';
                            if(status == 0) {
                                html += '<a class="iscomment">'+(topic.isComment?'允许评论':'禁止评论')+'</a>' +
                                    '<a class="del">删除</a>';
                            } else if(status == 1) {
                                html += '<a class="deepdel">彻底删除</a>';
                            }
                            html += '</div>' +
                                '<div style="clear: both;"></div>' +
                                '</div>' +
                                '</div>'
                        })
                        if (status == 0) {
                            if (data == null || data.list == null || data.list.length == 0) {
                                html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                                $(".layui-tab-item:first-child").html(html);
                                return false;
                            }
                            html += '<div id="published" class="text-center"></div>';
                            $(".layui-tab-item:first-child").html(html);
                            if (data.total>pageSize) {
                                laypage.render({
                                    elem: 'published',
                                    count: data.total,
                                    limit: pageSize,
                                    curr: pageNum,
                                    layout: ['count', 'prev', 'page', 'next', 'skip'],
                                    jump: function (obj, first) {
                                        if (!first) {
                                            page(0, obj.curr, pageSize)
                                            $('html').animate({scrollTop: 0}, 100)
                                        }
                                    }
                                });
                            }
                        } else if(status == 1) {
                            if (data == null || data.list == null || data.list.length == 0) {
                                html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                                $(".layui-tab-item:nth-child(2)").html(html);
                                return false;
                            }
                            html += '<div id="garbage" class="text-center"></div>';
                            $(".layui-tab-item:nth-child(2)").html(html);
                            if (data.total>pageSize) {
                                laypage.render({
                                    elem: 'garbage',
                                    count: data.total,
                                    limit: pageSize,
                                    curr: pageNum,
                                    layout: ['count', 'prev', 'page', 'next', 'skip'],
                                    jump: function (obj, first) {
                                        if (!first) {
                                            page(1, obj.curr, pageSize)
                                            $('html').animate({scrollTop: 0}, 100)
                                        }
                                    }
                                });
                            }
                        }
                    }
                    else{
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error: function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        }
        $(document).on("click",".iscomment",function() {
            var id=$(this).parent().parent().parent().attr('id');
            $this=$(this);
            $.ajax({
                url: "/forum/man/iscomment",
                type:"post",
                data:"id="+id,
                success:function (resp) {
                    if(resp.code == 0) {
                        var isComment=resp.data;
                        if(isComment){
                            $this.text("允许评论");
                        } else {
                            $this.text("禁止评论");
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
        })
        $(document).on("click",".del",function() {
            var id=$(this).parent().parent().parent().attr('id');
            $this = $(this);
            $.ajax({
                url: "/forum/man/del",
                type:"post",
                data:"id="+id,
                success:function (resp) {
                    if(resp.code == 0) {
                        $this.parent().parent().parent().remove();
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

        $(document).on("click",".deepdel",function() {
            var id=$(this).parent().parent().parent().attr('id');
            $this=$(this)
            $.ajax({
                url: "/forum/man/deepdel",
                type:"post",
                data:"id="+id,
                success:function (resp) {
                    if(resp.code == 0) {
                        $this.parent().parent().parent().remove();
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
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})