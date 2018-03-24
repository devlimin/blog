$(function(){
	$("#findPass").click(function(){
		findPassword();
	})

	$("#login-verify-btn").click(function() {
        $("#login-verify-img").attr("src","/account/verifyImg/2?v="+new Date());
        return false;
	})

	$("#reg-verify-btn").click(function() {
        $("#reg-verify-img").attr("src","/account/verifyImg/1?v="+new Date());
        return false;
	})

layui.use(['form','element', 'layer'], function(){

	var form = layui.form;
	var element = layui.element;
	var layer = layui.layer;

    $("#reg-email-btn").click(function() {
        var email = $.trim($("#reg-email-input").val());
        if ($.trim(email) == "") {
            layer.msg("邮箱不能为空");
            return false;
        }
        var url = "/account/emailCheck";
        $.ajax({
            url: url,
            data: "email="+email,
            type:"get",
            async:false,
            success:function (resp) {

                if (resp.code != 0) {
                    layer.msg(resp.msg);
                    return false;
                }
                layer.msg("该邮箱可用");
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员");
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
                    window.location.href="/index"
                } else {
                    layer.msg(resp.msg);
                }
            }
        })
		return false;
	})

	form.on('submit(regist)', function(data){
		var data = data.field;
		if(data.password != data.repassword) {
			layer.msg('重复密码不相等',{icon: 5},  function(){});
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
                    window.location.href="/index"
				} else {
					layer.msg(resp.msg);
				}
            }
		})
		return false;
	})
});

    function findPassword() {
    }

    function emailCheck(email) {
        if ($.trim(email) == "") {
            layer.msg("邮箱不能为空");
            return false;
        }
        var url = "/account/emailCheck";
        $.ajax({
            url: url,
            data: "email="+email,
            type:"get",
            async:false,
            success:function (resp) {

                if (resp.code != 0) {
                    layer.msg(resp.msg);
                    return false;
                }
                return true;
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员");
                return false;
            }
        })
    }
})