layui.use(['layer'], function(){

	var layer = layui.layer;


	$("#category-btn").click(function() {
		var category = $.trim($("#category-input").val());
		$("#category-input").val("");
		if(category == "") {
			layer.msg('个人分类不能为空',{icon:5}, function() {
				
			});
			return false;
		}
		$("#category-table").append('<tr> '+
										'<td>'+category+'</td>'+
										'<td>0</td>'+
										'<td>'+
											'<a href="#" class="cate-btn">编辑</a> | '+
											'<a href="#" class="cate-cal">删除</a>'+
										'</td>'+
									'</tr>');
	})
	var cateMap = new Map();
	$(document).on("click","#category-table tr td:nth-child(3) a:first-child",function() {
		var code = $(this).parent().parent().attr("id");
		if(cateMap.get(code) != null) {
			return false;
		}
		var cate = $(this).parent().parent().children("td:first-child").text();
		cateMap.set(code,cate);
		$(this).parent().parent().children("td:first-child")
				.replaceWith('<td>'+
								'<input type="text" name="" class="form-control cate" value="'+cate+'" style="width: 300px; display: inline-block;margin-right:10px;">' +
								'<a class="save cate-btn" href="#">保存</a> | <a class="cancle cate-btn" href="#">取消</a>'+
							'</td>');
	});
	$(document).on("click","#category-table tr td:nth-child(3) a:nth-child(2)",function() {
		$(this).parent().parent().remove();
	});

	$(document).on("click",".save",function() {
		
		category = $.trim($(this).prev().val());
		if(category == "") {
			layer.msg('个人分类不能为空',{icon:5}, function() {
				$(this).prev(".cate").focus();
			});
			return false;
		}
		var code = $(this).parent().parent().attr("id");
		cateMap.delete(code);

		$(this).parent().text(category);
	});
	$(document).on("click",".cancle",function() {
		var code = $(this).parent().parent().attr("id")
		var category = cateMap.get(code)
		$(this).parent().text(category);
		cateMap.delete(code)
	});

});
function hashcode(str) {
	var hash = 0, i, chr, len;
	if (str.length === 0) return hash;
	for (i = 0, len = str.length; i < len; i++) {
		chr   = str.charCodeAt(i);
		hash  = ((hash << 5) - hash) + chr;
		hash |= 0; // Convert to 32bit integer
	}
	return hash;
}