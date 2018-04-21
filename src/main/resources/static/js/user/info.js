$(function () {
    layui.use(['element', 'layer','upload'], function(){
        var element = layui.element;
        element.on('tab(info)', function(data){
            layid = this.getAttribute('lay-id');
            // location.hash = '='+ layid;
            if (layid == "basic"){

            }
            if (layid == "email"){

            }
            if (layid == "pass"){

            }
        })

        var upload = layui.upload;
        var uploadInst = upload.render({
            elem:'#headImg',
            url:"/user/man/updateHeadImg",
            method:'post',
            accept:'images',
            // before: function(obj){
            //     layer.load(); //上传loading
            // },
            //服务端响应信息、当前文件的索引、重新上传的方法
            done:function (resp,index,upload) {
                if(resp.code==0) {
                    $("#img").attr("src",resp.data);
                }
            },
            error:function (resp) {

            }
        })
    })
    $(".name-save").click(function () {
        var name = $.trim($(this).prev().val());
        if(name==""){
            layer.msg("用户名不能为空", {icon: 5,anim: 6});
            $(this).prev().focus();
            return false;
        }
        $this=$(this);
        $.ajax({
            url:"/user/man/updateName",
            data:"name="+name,
            type:"post",
            success:function (resp) {
                if(resp.code==0) {
                    $this.prev().val(resp.data);
                    layer.msg("用户名修改成功");
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            },error:function (resp) {
                layer.msg(resp.msg, {icon: 5,anim: 6});
            }
        })
    })
    $(".motto-save").click(function () {
        var motto = $.trim($(this).prev().val());
        if(motto==""){
            layer.msg("座右铭不能为空", {icon: 5,anim: 6});
            $(this).prev().focus();
            return false;
        }
        $this=$(this);
        $.ajax({
            url:"/user/man/updateMotto",
            data:"motto="+motto,
            type:"post",
            success:function (resp) {
                if(resp.code==0) {
                    $this.prev().val(resp.data);
                    layer.msg("座右铭修改成功");
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            },error:function (resp) {
                layer.msg(resp.msg, {icon: 5,anim: 6});
            }
        })
    })
})