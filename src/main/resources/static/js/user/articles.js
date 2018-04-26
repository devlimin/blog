$(function () {
    layui.use(['laypage', 'layer'], function () {
        var laypage = layui.laypage;
        var layer = layui.layer;
        page(1, 10);

        function page(pageNum, pageSize) {
            var uid = $("#uid").val();
            var cid = $("#cid").val();
            var url = "/article/page/" + uid;
            if (cid != "") {
                url += "/" + cid;
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
                            html += '<div class="article">' +
                                '<a href="/article/detail/' + article.id + '" class="title">' + article.title + '</a>' +
                                '<p class="detail">' + article.content + '...' +
                                '</p>' +
                                '<div class="other">' +
                                '<span>' + new Date(article.releaseDate).format() + '</span>' +
                                '<span class="layui-icon right">&#xe705 ' + article.readNum + '</span>' +
                                '<span class="layui-icon right">&#xe6b2 ' + article.commentNum + '</span>' +
                                '</div>' +
                                '</div>'
                        })
                        html += '<div class="text-center" id="page"></div>';
                        $("#article").html(html);
                        if (data.total>pageSize) {
                            laypage.render({
                                elem: 'page',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page(obj.curr, pageSize)
                                        $('html').animate({scrollTop: 0}, 100)
                                    }
                                }
                            });
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
        $("#mail").click(function () {
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
        Date.prototype.format = function () {
            return this.getFullYear() + "年" + (this.getMonth() + 1) + "月" + this.getDate() + "日 " + this.getHours() + ":" + this.getMinutes() + ":" + this.getSeconds();
        }
    })
})
