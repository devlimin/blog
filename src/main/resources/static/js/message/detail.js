$(function () {
    layui.use(['form','element', 'layer'], function(){
        var layer = layui.layer;
        var index;
        $("#letter-btn").click(function () {
            index = layer.open({
                type: 1,
                skin: 'layui-layer-rim', //加上边框
                area: ['420px', '300px'], //宽高
                title:"发私信",
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
            });
        })
        $(document).on("click","#send",function () {
            var toUserId = $("#toUserId").val();
            var content = $("#content").val();
            var data = "toUserId="+toUserId+"&content="+content;
            $.ajax({
                url:"/message/man/add",
                data:data,
                type:"post",
                success:function (resp) {
                    if(resp.code==0){
                        var messageVo = resp.data;
                        var html='<div class="my-msg">' +
                        '            <div class="msg" >' +
                    '                    <p class="content">'+messageVo.message.content+'</p>' +
                    '                    <span style="float: right;">'+new Date(messageVo.message.releaseDate).format()+'</span>' +
                    '                </div>' +
                    '                <a href="/article/list/'+messageVo.user.id+'">' +
                    '                    <img class="headImg" src="'+messageVo.user.headUrl+'"/></a>' +
                    '            </div>';
                        $("#messages").prepend(html);
                        layer.close(index);
                    }else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function (resp) {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            })
            layer.close(index);
        })
        $(document).on("click","#cancle",function () {
            layer.close(index);
        })
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})