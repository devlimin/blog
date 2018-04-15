
$(function () {
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
    window.wangEditor.fullscreen = {
        // editor create之后调用
        init: function(editorSelector){
            $(editorSelector + " .w-e-toolbar").prepend('<div class="w-e-menu"><a class="_wangEditor_btn_fullscreen" href="###" onclick="window.wangEditor.fullscreen.toggleFullscreen(\'' + editorSelector + '\')">全屏</a></div>');
        },
        toggleFullscreen: function(editorSelector){
            $(editorSelector).toggleClass('fullscreen-editor');
            if($(editorSelector + ' ._wangEditor_btn_fullscreen').text() == '全屏'){
                $(editorSelector + ' ._wangEditor_btn_fullscreen').text('退出全屏');
            }else{
                $(editorSelector + ' ._wangEditor_btn_fullscreen').text('全屏');
            }
        },
        source: function(editorSelector) {

        }
    };
    E.fullscreen.init('#editor');
    $("#editor .w-e-text-container").css("height","500");

layui.use('form', function(){
	var form = layui.form;
	form.verify({
        aritcle_type: function(value, item) {
            console.log(value);
            if (!value) {
                return "请选择文章类型";
            }
        },
        article_title:function (value,item) {
            console.log(value);
            if (!$.trim(value)){
                return "请输入文章标题";
            }
        },
        article_content:function (value,item) {
            console.log(value);
            if ($.trim(value)=="<p><br></p>"){
                return "请输入文章内容";
            }
        },
        article_sys:function (value,item) {
            if (!value){
                return "请选择文章分类";
            }
        }
    })
    var articleId = $("#aId").val();
    form.on('submit(publish)', function(data){
	    var url = "/article/man/publish";
        var data = $("form").serialize();
        if(articleId != undefined) {
            data += "&id="+articleId;
        }
        $.ajax({
            url: url,
            data: data,
            type:"post",
            success: function (resp) {
                if(resp.code == 0) {
                    window.location.href="/article/detail/"+resp.data;
                } else {
                    layer.msg(resp.msg);
                }
            }
        })
        return false;
    })
    form.on('submit(draft)', function(data){
        var url = "/article/man/draft";
        var data = $("form").serialize();
        $.ajax({
            url: url,
            data: data,
            type:"post",
            success: function (resp) {
                if(resp.code == 0) {
                    articleId = resp.data;
                    layer.msg("草稿保存成功！")
                } else {
                    layer.msg(resp.msg);
                }
            }
        })
        return false;
    })
    form.on('submit(save)', function(data){
        var url = "/article/man/publish";
        var data = $("form").serialize() + "&id="+articleId;
        $.ajax({
            url: url,
            data: data,
            type:"post",
            success: function (resp) {
                if(resp.code == 0) {
                    articleId = resp.data;
                    layer.msg("修改保存成功！")
                } else {
                    layer.msg(resp.msg);
                }
            }
        })
        return false;
    })
});
})