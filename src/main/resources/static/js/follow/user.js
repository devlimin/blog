$(function(){
    layui.use(['laypage','layer'], function() {
        var laypage = layui.laypage;
        var layer = layui.layer;

        var toUserId;
        var index;
        $(".mail").click(function () {
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            toUserId =$(this).prev(".toUserId").val();
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
            toUserId = null;
        })
        $(document).on("click", "#cancle", function () {
            layer.close(index);
        })

        $(document).on("click",".follow,.unfollow",function () {
            var loginUserId= $("#loginUserId").val();
            if (loginUserId=="") {
                window.location.href="/account?action=login&next="+window.location.pathname;
                return false;
            }
            var entityId = $(this).prev().prev(".toUserId").val();
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
                            $this.addClass("unfollow")
                            $this.addClass("layui-btn-primary");;
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

        $(document).on("click",".black,.white",function () {
            var entityId =$(this).prev(".toUserId").val();
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
                        } else if($this.hasClass("white")) {
                            $this.removeClass("white");
                            $this.addClass("black")
                            $this.removeClass("layui-btn-primary")
                            $this.text("已拉黑")
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

        var pageNum = $("#pageNum").val();
        if(parseInt($("#total").val())>parseInt($("#pageSize").val())){
            laypage.render({
                elem: 'page',
                count: $("#total").val(),
                limit: $("#pageSize").val(),
                curr: pageNum,
                layout: ['count', 'prev', 'page', 'next', 'skip'],
                jump: function (obj, first) {
                    if(!first) {
                        var type = $("#type").val();
                        window.location.href = "/follow/man/" + type + "/?pageNum=" + obj.curr;
                    }
                }
            });
        }
    })
})