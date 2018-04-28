$(function () {
    layui.use(['laypage','layer'], function() {
        var laypage = layui.laypage;
        var layer = layui.layer;
        $(".follow").click(function () {
            var entityId = $(this).prev(".entityId").val();
            var type=$("#type").val();
            $this=$(this);
            var url=null;
            if($this.hasClass("unfollow")){
                if(type=="article"){
                    url="/follow/man/markArticle"
                } else if(type=="topic"){
                    url="/follow/man/markTopic"
                }
            } else if ($this.hasClass("follow")){
                if(type=="article"){
                    url="/follow/man/unmarkArticle"
                } else if(type=="topic"){
                    url="/follow/man/unmarkTopic"
                }
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
                            $this.text("已收藏");
                        } else if ($this.hasClass("follow")){
                            $this.removeClass("follow");
                            $this.addClass("unfollow");
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
    });
})