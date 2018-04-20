$(function(){
	$("#findPass").click(function(){
		findPassword();
	})

	$("#login-verify-btn").click(function() {
        $("#login-verify-img").attr("src","/account/verifyImg/login?v="+new Date());
        return false;
	})

	$("#reg-verify-btn").click(function() {
        $("#reg-verify-img").attr("src","/account/verifyImg/regist?v="+new Date());
        return false;
	})

layui.use(['form','element', 'layer'], function(){
	var element = layui.element;
	var layer = layui.layer;
    var form = layui.form;
    form.verify({
        pass:function (value,item) {
            if(!new RegExp("^[\\S]{6,12}$").test(value)){
                return "密码必须6到12位，且不能出现空格"
            }
        }
    })

    $("#reg-email-btn").click(function() {
        var email = $.trim($("#reg-email-input").val());
        if ($.trim(email) == "") {
            layer.msg('邮箱不能为空', {icon: 5,anim: 6});
            return false;
        }
        if(!new RegExp("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$").test(email)){
            layer.msg('邮箱格式不正确', {icon: 5,anim: 6});
            return false;
        }
        var url = "/account/emailCheck";
        $.ajax({
            url: url,
            data: "email="+email,
            type:"get",
            success:function (resp) {
                if (resp.code == 0) {
                    layer.msg("该邮箱可用");
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
                return false;
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                return false;
            }
        })
        return false;
    })

	form.on('submit(login)', function(data){
        var data = data.field;
        var url = "/account/login";
        $.ajax({
            url: url,
            data: data,
            type: "post",
            success: function (resp) {
                if(resp.code == 0) {
                    if(resp.data!=""){
                        window.location.href=resp.data;
                    } else {
                        window.location.href="/index"
                    }
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
            }
        })
		return false;
	})

	form.on('submit(regist)', function(data){
		var data = data.field;
		if(data.password != data.repassword) {
			layer.msg('重复密码不相等',{icon: 5,anim: 6});
			$("#repassword").focus();
			return false;
		}
        var url = "/account/regist";
        $.ajax({
			url: url,
			data: data,
			type: "post",
			success: function (resp) {
				if(resp.code == 0) {
                    window.location.href="/account"
				} else {
					layer.msg(resp.msg,{icon: 5,anim: 6});
				}
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
            }
		})
		return false;
	})
});
    function findPassword() {
    }
})