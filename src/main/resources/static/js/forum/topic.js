$(function () {
    layui.use(['laypage','layer'], function(){
        var laypage = layui.laypage;
        var layer = layui.layer;
        var total;
        function page(pageNum,pageSize) {
            var data="topicId="+$("#topicId").val()+"&pageNum="+pageNum+"&pageSize="+pageSize
            $.ajax({
                url:"/forum/reply",
                data:data,
                type:"get",
                success:function (resp) {
                    if(resp.code==0){
                        var data = resp.data;
                        total = data.total;
                        var html = "";
                        if(data == null || data.list == null || data.list.length == 0) {
                            html = "<div id='no-reply' style='text-align: center;padding-top: 40px;padding-bottom: 30px;background-color: white;'>暂无回帖</div>";
                            $("#replies").html(html);
                            return false;
                        }
                        endRow = data.total-(pageNum-1)*pageSize;
                        $.each(data.list, function (i,reply) {
                            html+='<div class="reply">' +
                                        '<a><img class="reply-user-img" src="'+reply.userHeadUrl+'"></a>' +
                                        '<div>' +
                                            '<a class="reply-user-name">'+reply.userName+'</a>' +
                                            '<p class="reply-content">'+reply.content+'</p>' +
                                            '<span>发表于'+new Date(reply.releaseDate).format()+'</span>' +
                                            '<span class="pull-right">'+(endRow--)+'楼</span>' +
                                        '</div>' +
                                        '<hr>' +
                                    '</div>'
                        })
                        html +='<div class="text-center" id="page"></div>';
                        $("#replies").html(html);
                        if (data.total>pageSize){
                            laypage.render({
                                elem: 'page',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page(obj.curr, pageSize);
                                        $('html').animate({scrollTop: $("#reply").offset().top}, 100)
                                    }
                                }
                            });
                        }
                        $('#replies').animate({ scrollTop: 0 }, 100)
                    }else{
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        }
        page(1,10)
        $("#reply-btn").click(function () {
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            var content = $.trim($(this).prev().val());
            $(this).prev().val("");
            if (content==""){
                layer.msg("回帖不能为空", {icon: 5,anim: 6});
                return false;
            }
            var data = "content="+content+"&topicId="+$("#topicId").val();
            $.ajax({
                url:"/forum/man/reply",
                data:data,
                type:"post",
                success:function (resp) {
                    if(resp.code==0){
                        var reply = resp.data;
                        var html = '<div class="reply">' +
                                        '<a><img class="reply-user-img" src="'+reply.userHeadUrl+'"></a>' +
                                        '<div>' +
                                            '<a class="reply-user-name">'+reply.userName+'</a>' +
                                            '<p class="reply-content">'+reply.content+'</p>' +
                                            '<span>发表于'+new Date(reply.releaseDate).format()+'</span>' +
                                            '<span class="pull-right">'+(++total)+'楼</span>' +
                                        '</div>' +
                                        '<hr>' +
                                    '</div>'
                        $("#replies").prepend(html);
                        if($("#no-reply").length==1){
                            $("#no-reply").remove();
                            total++;
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                }
            })
        })

        var index;
        $(document).on("click","#mail",function () {
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            index = layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: ['420px', '300px'], //宽高
                title: "发私信",
                content: ' <div style="margin: 10px">' +
                '<div class="layui-form-item layui-form-text">\n' +
                '    <label class="layui-form-label" style="padding-left: 0px;width: 50px;">内容</label>\n' +
                '    <div class="layui-input-block" style="margin-left: 60px;">\n' +
                '      <textarea id="msg_content" style="height: 150px;" placeholder="请输入内容" class="layui-textarea"></textarea>\n' +
                '    </div>\n' +
                '  </div>' +
                '  <div class="layui-form-item">\n' +
                '    <div class="layui-input-block" style="margin-left: 60px;">\n' +
                '      <button id="send" class="layui-btn">发送</button>\n' +
                '      <button id="cancle" class="layui-btn layui-btn-primary">取消</button>\n' +
                '    </div>\n' +
                '  </div>' +
                '</div> '
            })
        })
        $(document).on("click", "#send", function () {
            var toUserId = $("#toUserId").val();
            var content = $.trim($("#msg_content").val());
            if(content=="") {
                layer.msg("不能为空", {icon: 5,anim: 6});
                return false;
            }
            var data = "toUserId=" + toUserId + "&content=" + content;
            $.ajax({
                url: "/message/man/add",
                data: data,
                type: "post",
                success: function (resp) {
                    if (resp.code == 0) {
                        var messageVo = resp.data;
                        var html = '<div class="my-msg">' +
                            '            <div class="msg" >' +
                            '                    <p class="content">' + messageVo.message.content + '</p>' +
                            '                    <span style="float: right;">' + new Date(messageVo.message.releaseDate).format() + '</span>' +
                            '                </div>' +
                            '                <a href="/article/list/' + messageVo.user.id + '">' +
                            '                    <img class="headImg" src="' + messageVo.user.headUrl + '"/></a>' +
                            '            </div>';
                        $("#messages").prepend(html);
                        layer.close(index);
                    } else {
                        layer.msg(resp.msg, {icon: 5, anim: 6});
                    }
                },
                error: function (resp) {
                    layer.msg(resp.msg, {icon: 5, anim: 6});
                }
            })
            layer.close(index);
        })
        $(document).on("click", "#cancle", function () {
            layer.close(index);
        })

        $(document).on("click","#follow",function () {
            var entityId = $("#toUserId").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            var url=null;
            if($this.hasClass("unfollow")){
                url="/follow/man/followuser"
            } else if ($this.hasClass("follow")){
                url="/follow/man/unfollowuser"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success: function (resp) {
                    if(resp.code == 0) {
                        if($this.hasClass("unfollow")){
                            $this.removeClass("unfollow");
                            $this.removeClass("layui-btn-primary");
                            $this.addClass("follow");
                            $this.text("已关注");
                        } else if ($this.hasClass("follow")){
                            $this.removeClass("follow");
                            $this.addClass("unfollow");
                            $this.addClass("layui-btn-primary");
                            $this.text("关注");
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
        $("#black").click(function () {
            var entityId = $("#toUserId").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            var url = null;
            if($this.hasClass("white")){
                url="/follow/man/black"
            } else if($this.hasClass("black")){
                url="/follow/man/white"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success:function (resp) {
                    if (resp.code==0){
                        if($this.hasClass("black")) {
                            $this.removeClass("black");
                            $this.addClass("white")
                            $this.addClass("layui-btn-primary")
                            $this.text("黑名单")
                            $this.before('<button id="follow" class="unfollow layui-btn layui-btn-primary layui-btn-xs">关注</button>' +
                                '<button class="layui-btn layui-btn-xs" id="mail">私信</button>')
                            $("#top>span").after('<button style="margin-top: 25px;" id="mark"\n' +
                                'class="unMark layui-btn layui-btn-primary pull-right">收藏</button>')
                        } else if($this.hasClass("white")) {
                            $this.removeClass("white");
                            $this.addClass("black")
                            $this.removeClass("layui-btn-primary")
                            $this.text("已拉黑")
                            $("#follow").remove();
                            $("#mail").remove();
                            $("#mark").remove();
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
        $(document).on("click","#mark",function () {
            var entityId = $("#topicId").val();
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            $this=$(this);
            if($this.hasClass("unMark")){
                var url="/follow/man/markTopic"
            } else if ($this.hasClass("Mark")){
                var url="/follow/man/unmarkTopic"
            } else {
                return false;
            }
            $.ajax({
                url:url,
                data:"entityId="+entityId,
                type:"post",
                success: function (resp) {
                    if(resp.code == 0) {
                        if($this.hasClass("unMark")){
                            $this.removeClass("unMark");
                            $this.removeClass("layui-btn-primary");
                            $this.addClass("Mark");
                            $this.text("已收藏");
                        } else if ($this.hasClass("Mark")){
                            $this.removeClass("Mark");
                            $this.addClass("unMark");
                            $this.addClass("layui-btn-primary");
                            $this.text("收藏");
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})