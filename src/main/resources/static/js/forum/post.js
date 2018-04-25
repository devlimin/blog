$(function(){
    var E = window.wangEditor
    var editor = new E('#editor')
    var content = $("#content")
    editor.customConfig.onchange =function (html) {
        content.val(html);
    }
    editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
    editor.customConfig.zIndex = 0;
    editor.customConfig.menus = [
        'head',  // 标题
        'bold',  // 粗体
        'fontSize',  // 字号
        'fontName',  // 字体
        'italic',  // 斜体
        'underline',  // 下划线
        'strikeThrough',  // 删除线
        'foreColor',  // 文字颜色
        'backColor',  // 背景颜色
        'link',  // 插入链接
        'list',  // 列表
        'justify',  // 对齐方式
        'quote',  // 引用
        // 'emoticon',  // 表情
        'image',  // 插入图片
        'table',  // 表格
        // 'video',  // 插入视频
        'code',  // 插入代码
        'undo',  // 撤销
        'redo'  // 重复
    ]
    editor.create();
    content.val(editor.txt.html());
    $("#editor .w-e-text-container").css("height","500");
    layui.use(['form','layer'], function(){
        var layer = layui.layer;
        var form = layui.form;
        form.verify({
            forum_title:function (value,item) {
                console.log(value);
                if (!$.trim(value)){
                    return "请输入帖子标题";
                }
            },
            forum_content:function (value,item) {
                console.log(value);
                if ($.trim(value)=="<p><br></p>"){
                    return "请输入帖子内容";
                }
            },
            forum_theme:function (value,item) {
                if (!value){
                    return "请选择";
                }
            }
        })
        form.on('submit(publish)', function(data){
            var url = "/forum/man/post";
            var data = $("form").serialize();
            $.ajax({
                url: url,
                data: data,
                type:"post",
                success: function (resp) {
                    if(resp.code == 0) {
                        window.location.href="/forum/topic/"+resp.data;
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                },
                error:function () {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                }
            })
            return false;
        })
    })
})