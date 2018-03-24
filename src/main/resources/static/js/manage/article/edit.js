layui.use('form', function(){
	var form = layui.form;
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

    $("#post").click(function(){
        if ($('#summernote').summernote('isEmpty')) {
            alert('editor content is empty');
        }
        var content = $("#summernote").summernote('code');
        console.log(content)
    })
});