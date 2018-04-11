
layui.use(['layer','laypage'], function(){
	var layer = layui.layer;
	//新增分类
	$("#category-btn").click(function() {
		var category = $.trim($("#category-input").val());
		$("#category-input").val("");
		if(category == "") {
			layer.msg('个人分类不能为空',{icon:5}, function() {
                $("#category-btn").focus();
			});
			return false;
		}
		var url="/category/man/add";
		var data = "name="+category;
		$.ajax({
			url: url,
			data: data,
			type: "post",
			success: function (resp) {
				if (resp.code == 0) {
                    $("#category-table").append('<tr id="'+resp.data+'"> '+
														'<td>'+category+'</td>'+
														'<td>0</td>'+
														'<td>'+
															'<a href="#" class="cate-btn">编辑</a> | '+
															'<a href="#" class="cate-cal">删除</a>'+
														'</td>'+
													'</tr>');
				} else {
					layer.msg(resp.msg);
				}
            }
		})
	})

	var cateMap = new Map();
	//编辑
	$(document).on("click","#category-table tr td:nth-child(3) a:first-child",function() {
		var id = $(this).parent().parent().attr("id");
		if(cateMap.get(id) != null) {
			return false;
		}
		var cate = $(this).parent().parent().children("td:first-child").text();
		cateMap.set(id,cate);
		$(this).parent().parent().children("td:first-child")
				.replaceWith('<td>'+
								'<input type="text" name="" class="form-control cate" value="'+cate+'" style="width: 300px; display: inline-block;margin-right:10px;">' +
								'<a class="save cate-btn" href="#">保存</a> | <a class="cancle cate-btn" href="#">取消</a>'+
							'</td>');
	});

	//更改分类
	$(document).on("click",".save",function() {
		category = $.trim($(this).prev().val());
		if(category == "") {
			layer.msg('个人分类不能为空',{icon:5}, function() {
				$(this).prev(".cate").focus();
			});
			return false;
		}
		var id = $(this).parent().parent().attr("id");
		var url = "/category/man/update";
		var data = "name="+category+"&id="+id;
		var $td = $(this).parent();
		$.ajax({
			url: url,
			data: data,
			type: 'post',
			success: function (resp) {
				if (resp.code == 0) {
                    cateMap.delete(id);
                    $td.text(category);
				} else {
                    layer.msg(resp.msg);
				}
            }
		})
	});
	//取消保存
	$(document).on("click",".cancle",function() {
		var id = $(this).parent().parent().attr("id")
		var category = cateMap.get(id)
		$(this).parent().text(category);
		cateMap.delete(id)
	});

    //删除
    $(document).on("click","#category-table tr td:nth-child(3) a:nth-child(2)",function() {
        var id = $(this).parent().parent().attr("id");
        var $tr = $(this).parent().parent();
        $.ajax({
			url: "/category/man/delete",
			data: "id="+ id,
			type: "post",
			success: function (resp) {
				if (resp.code == 0) {
                    $tr.remove();
				} else {
                    layer.msg(resp.msg);
				}
            }
		})
    });

});