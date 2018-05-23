$(function () {
    layui.use(['layer'], function(){
        var layer = layui.layer;
        $("#login").click(function () {
            var data = $("form").serialize();
            console.log(data);
            $.ajax({
                url:'/admin/login',
                data:data,
                type:'post',
                success:function (resp) {
                    if(resp.code==0) {
                        window.location.href="/admin/user/info";
                    } else {
                        layer.msg(resp.msg, {icon: 5, anim: 6});
                    }
                },
                error: function (resp) {
                    layer.msg(resp.msg, {icon: 5, anim: 6});
                }
            })
            return false;
        })
    })

})