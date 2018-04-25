$(function () {

    layui.use(['laypage'], function(){
        var laypage = layui.laypage;
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
                        var themeId = $("#themeId").val();
                        if (themeId != "") {
                            window.location.href = "/forum/" + themeId + "/?pageNum=" + obj.curr;
                        } else {
                            window.location.href = "/forum/?pageNum=" + obj.curr;
                        }
                    }
                }
            });
        }

    })
})