layui.use('laypage', function(){
	var laypage = layui.laypage;
	laypage.render({
		elem: 'page',
		count: 10000,
		limit: 5,
		layout: ['count', 'prev', 'page', 'next', 'skip'],
		jump: function(obj){
			console.log(obj);
					//ajax
					var cur = obj.curr;
					var limit = obj.limit;
				}
			});
});
