$(function () {
    layui.use(['element', 'layer','upload'], function(){
        var element = layui.element;
        var layer = layui.layer;
        // element.on('tab(info)', function(data){
        //     layid = this.getAttribute('lay-id');
        //     // location.hash = '='+ layid;
        //     if (layid == "basic"){
        //
        //     }
        //     if (layid == "email"){
        //
        //     }
        //     if (layid == "pass"){
        //
        //     }
        // })

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
        $("#passupdate").click(function () {
            var sourcepass = $.trim($("#sourcepass").val());
            if (sourcepass==""){
                layer.msg("原密码不能为空", {icon: 5,anim: 6});
                return false;
            }
            var newpass = $.trim($("#newpass").val());
            if (newpass==""){
                layer.msg("新密码不能为空", {icon: 5,anim: 6});
                return false;
            } else if(sourcepass==newpass){
                layer.msg("新密码不能不原密码相同", {icon: 5,anim: 6});
                return false;
            }
            if(!new RegExp("^[\\S]{6,12}$").test(newpass)){
                return "密码必须6到12位，且不能出现空格"
            }
            var repeatpass = $.trim($("#repeatpass").val());
            if (repeatpass==""){
                layer.msg("重复密码不能为空", {icon: 5,anim: 6});
                return false;
            } else if(repeatpass!=newpass){
                layer.msg("重复密码必须和新密码相同", {icon: 5,anim: 6});
                return false;
            }
            var data = "sourcepass="+sourcepass+"&newpass="+newpass;
            $.ajax({
                url:"/user/man/passupdate",
                data:data,
                type:"post",
                success:function (resp) {
                    if (resp.code==0) {
                        layer.msg("密码修改成功！")
                    } else {
                        layer.msg(resp.msg,{icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
        })
    })
})