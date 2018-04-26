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
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})