$(function () {
    layui.use(['layer', 'laypage'], function () {
        var layer = layui.layer;
        var laypage = layui.laypage;
        var pageNum = $("#pageNum").val();
        var total = $("#total").val();
        var pageSize = $("#pageSize").val();
        if (parseInt(total) > parseInt(pageSize)) {
            laypage.render({
                elem: 'page',
                count: total,
                limit: pageSize,
                curr: pageNum,
                layout: ['count', 'prev', 'page', 'next', 'skip'],
                jump: function (obj, first) {
                    if (!first) {
                        var type=$("#type").val()
                        window.location.href = "/message/man/conversation?type="+type+"&pageNum=" + obj.curr;
                    }
                }
            });
        }
    })
})