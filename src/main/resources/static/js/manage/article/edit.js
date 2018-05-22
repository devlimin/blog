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
            $(editorSelector + " .w-e-toolbar").prepend('' +
                '<div class="w-e-menu">' +
                    '<a class="_wangEditor_btn_fullscreen" href="###" ' +
                        'onclick="window.wangEditor.fullscreen.toggleFullscreen(\'' + editorSelector + '\')">全屏' +
                '</a></div>');
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
    var wangeditor = $("#wangeditor").remove();

    var html = $(".editormd-markdown-textarea").val();
    if(html!=null){
        var turndownService = new TurndownService()
        var markdown = turndownService.turndown(html)
        $(".editormd-markdown-textarea").val(markdown);
    }
    var md_editor = editormd("my-editormd", {//注意1：这里的就是上面的DIV的id属性值
        width   : "100%",
        height  : 640,
        syncScrolling : "single",
        path    : "/editormd/lib/",//注意2：你的路径
        saveHTMLToTextarea : true,//注意3：这个配置，方便post提交表单

        /**上传图片相关配置如下*/
        imageUpload : true,
        imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
        imageUploadURL : "/smart-api/upload/editormdPic/",//注意你后端的上传图片服务地址

        onfullscreen : function() {
            $("#my-editormd").css({"z-index":"10000"})
        },
        onfullscreenExit : function() {
            $("#my-editormd").css({"z-index":"0"})
        }
    });
//markdown的html格式的博文
    $(".editormd-html-textarea").attr("name", "content");
    $(".editormd-markdown-textarea").attr("name", "");

    var mdeditor;
    $("#editor-btn").click(function () {
        var text = $(this).text();
        if(text=="markdown") {
            $(this).text("富文本");
            var html = $("textarea[name='content']").val();
            mdeditor = $("#my-editormd").remove();
            $("#editor-area").append(wangeditor);
            editor.txt.html(html)
        } else if(text=="富文本") {
            $(this).text("markdown");
            var html = editor.txt.html();
            wangeditor = $("#wangeditor").remove();
            $("#editor-area").append('<div id="my-editormd">\n' +
                '\t\t\t\t<textarea class="editormd-markdown-textarea">[(${article}?${article.content})]</textarea>\n' +
                '\t\t\t\t<textarea class="editormd-html-textarea" lay-verify="article_content"></textarea>\n' +
                '\t\t\t</div>');
            var turndownService = new TurndownService()
            var markdown = turndownService.turndown(html)
            // $("textarea[class='editormd-html-textarea editormd-markdown-textarea']").val(markdown);
            $(".editormd-markdown-textarea").val(markdown+" ");
            md_editor = editormd("my-editormd", {//注意1：这里的就是上面的DIV的id属性值
                width   : "100%",
                height  : 640,
                syncScrolling : "single",
                path    : "/editormd/lib/",//注意2：你的路径
                saveHTMLToTextarea : true,//注意3：这个配置，方便post提交表单

                /**上传图片相关配置如下*/
                imageUpload : true,
                imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                imageUploadURL : "/smart-api/upload/editormdPic/",//注意你后端的上传图片服务地址

                onfullscreen : function() {
                    $("#my-editormd").css({"z-index":"10000"})
                },
                onfullscreenExit : function() {
                    $("#my-editormd").css({"z-index":"0"})
                }
            });
            $(".editormd-html-textarea").attr("name", "content");
            $(".editormd-markdown-textarea").attr("name", "");
        }
        return false;
    })

layui.use(['form','layer'], function(){
	var form = layui.form;
	var layer = layui.layer;
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
            if ($.trim(value)=="<p><br></p>"||$.trim(value)==""){
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
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
            },
            error:function () {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
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