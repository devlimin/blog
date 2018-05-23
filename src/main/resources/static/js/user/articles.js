$(function () {
    layui.use(['laypage', 'layer','element'], function () {
        var laypage = layui.laypage;
        var layer = layui.layer;
        var element = layui.element;

        page(0, 1, 10);

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

        function page(status,pageNum, pageSize) {
            var uid = $("#uid").val();
            var cid = $("#cid").val();
            var url=null;
            if (status==0) {
                url = "/article/page/" + uid;
                if (cid != "") {
                    url += "/" + cid;
                }
            } else if(status==1){
                url="/forum/page/"+uid;
            }

            $.ajax({
                url: url,
                data: "pageNum=" + pageNum + "&pageSize=" + pageSize,
                type: "get",
                success: function (resp) {
                    if (resp.code == 0) {
                        var data = resp.data;
                        var html = ""
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>暂无数据</div>";
                            $("#article").html(html);
                            return false;
                        }
                        $.each(data.list, function (i, article) {
                            html += '<div class="article">';
                            if(status==0) {
                                html+='<a href="/article/detail/' + article.id + '" class="title">' + article.title + '</a>';
                            }else if(status==1){
                                html+='<a href="/forum/topic/'+ article.id + '" class="title">' + article.title + '</a>';
                            }
                                html+='<p class="detail">' + article.content + '...' +
                                '</p>' +
                                '<div class="other">' +
                                '<span>' + new Date(article.releaseDate).format() + '</span>' +
                                '<span class="layui-icon right">&#xe705 ' + article.readNum + '</span>' +
                                '<span class="layui-icon right">&#xe6b2 ' + article.commentNum + '</span>' +
                                '</div>' +
                                '</div>'
                        })
                        if (status == 0) {
                            if (data == null || data.list == null || data.list.length == 0) {
                                html = "<div style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>该分类暂无数据</div>";
                                $("#article").html(html);
                                return false;
                            }
                            html += '<div id="articlePage" class="text-center"></div>';
                            $("#article").html(html);
                            if (data.total>pageSize) {
                                laypage.render({
                                    elem: 'articlePage',
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
                            html += '<div id="topicPage" class="text-center"></div>';
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
                                            page(1, obj.curr, pageSize)
                                            $('html').animate({scrollTop: 0}, 100)
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5, anim: 6});
                    }
                },
                error: function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5, anim: 6});
                }
            })
        }

        var index;
        $(document).on("click","#mail",function () {
            index = layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: ['420px', '300px'], //宽高
                title: "发私信",
                content: ' <div style="margin: 10px">' +
                '<div class="layui-form-item layui-form-text">\n' +
                '    <label class="layui-form-label" style="padding-left: 0px;width: 50px;">内容</label>\n' +
                '    <div class="layui-input-block" style="margin-left: 60px;">\n' +
                '      <textarea id="content" style="height: 150px;" placeholder="请输入内容" class="layui-textarea"></textarea>\n' +
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
            var content = $.trim($("#content").val());
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
            if($this.hasClass("unfollow")){
                var url="/follow/man/followuser"
            } else if ($this.hasClass("follow")){
                var url="/follow/man/unfollowuser"
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
                            $this.before('<button id="follow" class="unfollow layui-btn layui-btn-primary layui-btn-sm">关注</button>' +
                                '<button class="layui-btn layui-btn-sm" id="mail">私信</button>')
                        } else if($this.hasClass("white")) {
                            $this.removeClass("white");
                            $this.addClass("black")
                            $this.removeClass("layui-btn-primary")
                            $this.text("已拉黑")
                            $("#follow").remove();
                            $("#mail").remove();
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

        Date.prototype.format = function () {
            return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
        }
    })
})
