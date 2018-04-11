layui.use('form', function(){
	var form = layui.form;
    form.on('submit(publish)', function(data){
	    var url = "/article/man/publish";
        var data = data.field;
        if(data.cId == null) {
            layer.msg('用户分类不能为空');
        }
        var data = $("form").serialize();
        console.log(data);
        $.ajax({
            url: url,
            data: data,
            type:"post",
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
$(document).ready(function() {
    $('#summernote').summernote({
        height : 300,
        width: 1005,
        minHeight : null,
        maxHeight : null,
        focus : true,
        lang : 'zh-CN',
        toolbar: [
            // [groupName, [list of button]]
            ['misc',['fullscreen','codeview','undo','redo']],
            ['font', ['fontname','fontsize','color','bold', 'italic', 'underline', 'strikethrough','superscript','subscript','clear']],
            ['para', ['style','ul', 'ol', 'paragraph','height']],
            ['insert',['picture','link','table','hr']],
        ],
        callbacks: {

        }
    });

    // $("#post").click(function(){
    //     if ($('#summernote').summernote('isEmpty')) {
    //         alert('editor content is empty');
    //     }
    //     var content = $("#summernote").summernote('code');
    //     console.log(content)
    // })

});