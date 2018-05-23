$(function () {
    layui.use(['layer','laypage','laydate','element','form'], function(){
        var laypage = layui.laypage;
        var element = layui.element;
        var laydate = layui.laydate;
        var form = layui.form;
        //执行一个laydate实例
        //同时绑定多个
        lay('.time-item').each(function(){
            laydate.render({
                elem: this,
                type: 'datetime',
                max:0,
                trigger: 'click'
            });
        });

        var index;
        $("#add_btn").click(function () {
            var html='<div style="margin-left: 40px;margin-top: 30px;"><input type="text" id="sysCategoryName"' +
                        ' class="layui-input" style="display: inline-block;width: 300px;"/>' +
                        '<button class="layui-btn" id="add_sysCategory">新增</button></div>';
            index = layer.open({
                type: 1,
                skin: 'layui-layer-molv', //加上边框
                area: ['550px', '200px'], //宽高
                title: "添加系统分类",
                content:html
            })
            return false;
        })
        $(document).on("click","#add_sysCategory",function () {
            var sysCategoryName = $.trim($("#sysCategoryName").val());
            if(sysCategoryName=="") {
                layer.msg("系统分类名不能为空", {icon: 5,anim: 6});
                return false;
            }
            $.ajax({
                url:'/admin/article/sysAdd',
                data:'name='+sysCategoryName,
                type:'post',
                success:function (resp) {
                    if (resp.code==0) {
                        layer.msg('添加成功', {
                            icon: 1,
                            time: 1000
                        });
                        layer.close(index)
                    }else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                    return false;
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                    return false;
                }
            })
        })

        $("#search_btn").click(function () {
            page(1,pageNum);
            return false;
        })
        page(1,pageNum);
        function page(pageNum,pageSize) {
            if(!isPositiveInteger($("input[name='cid']").val())){
                layer.msg('分类id为正整数', {icon: 5,anim: 6});
                return false;
            }
            var data = $("form").serialize();
            data += "&pageNum="+pageNum+"&pageSize="+pageSize
            $.ajax({
                url:'/admin/article/sysCategoryPage',
                type:"get",
                data:data,
                success:function (resp) {
                    if (resp.code==0) {
                        var html='';
                        var data = resp.data;
                        $("#nocontent").remove();
                        if (data == null || data.list == null || data.list.length == 0) {
                            html = "<div id='nocontent' style='text-align: center;margin-top: 40px;margin-bottom: 30px;'>暂无数据</div>";
                            $("#table").append(html);
                            $("#page").remove();
                            $("#thead").css({"display":"none"});
                            $("#table tr:not(:first)").remove();
                            return false;
                        }
                        $("#thead").css({"display":"table-row"});
                        $.each(data.list,function (i, sysCategory) {
                            html+='<tr id="'+sysCategory.id+'">'
                                +'<td>'+sysCategory.id+'</td>'
                                +'<td>'+sysCategory.name+'</td>'
                            if(sysCategory.status==-1){
                                html+='<td>已删除</td>'+
                                    '<td>' +
                                    '<button class="layui-btn layui-btn-xs layui-btn-primary enable">启用</button>' +
                                    '<button class="layui-btn layui-btn-xs updatename">更改名称</button>' +
                                    '</td>'
                            } else if(sysCategory.status==0){
                                html+='<td>已发表</td>' +
                                    '<td>' +
                                    '<button class="layui-btn layui-btn-xs disable">禁用</button>' +
                                    '<button class="layui-btn layui-btn-xs updatename">更改名称</button>' +
                                    '</td>'
                            }
                            html+='</tr>'
                        })
                        $("#table tr:not(:first)").remove();
                        $("#table").append(html);
                        $("#page").remove();
                        if (data.total>pageSize) {
                            $("#table").after('<div id="page" class="text-center"></div>');
                            laypage.render({
                                elem: 'page',
                                count: data.total,
                                limit: pageSize,
                                curr: pageNum,
                                layout: ['count', 'prev', 'page', 'next', 'skip'],
                                jump: function (obj, first) {
                                    if (!first) {
                                        page( obj.curr, pageSize)
                                    }
                                }
                            });
                        }
                    } else {
                        layer.msg(resp.msg, {icon: 5,anim: 6});
                    }
                    return false;
                },
                error:function (resp) {
                    layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                    return false;
                }
            })
        }
    });

    $(document).on("click",".disable",function () {
        var msgId = $(this).parent().parent().attr("id");
        $this = $(this);
        $.ajax({
            url:'/admin/article/sysDisable',
            type:'get',
            data:'id='+msgId,
            success:function (resp) {
                if(resp.code==0){
                    $this.removeClass("disable")
                    $this.addClass("enable");
                    $this.addClass("layui-btn-primary");
                    $this.text("启用")
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
                return false;
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                return false;
            }
        })
    })

    $(document).on("click",".enable",function () {
        var msgId = $(this).parent().parent().attr("id");
        $this=$(this);
        $.ajax({
            url:'/admin/article/sysEnable',
            type:'get',
            data:'id='+msgId,
            success:function (resp) {
                if(resp.code==0){
                    $this.removeClass("enable")
                    $this.removeClass("layui-btn-primary");
                    $this.addClass("disable");
                    $this.text("禁用")
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
                return false;
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                return false;
            }
        })
    })

    var tr;
    var scIdName
    var index;
    $(document).on("click",".updatename",function () {
        var scId = $(this).parent().parent().attr("id");
        scIdName = scId;
        var scName = $(this).parent().parent().children("td:nth-child(2)").text();
        tr = $(this).parent().parent();
        index = layer.open({
            type: 1,
            skin: 'layui-layer-molv', //加上边框
            area: ['500px', '250px'], //宽高
            title: "更改名称",
            content:
            '<table class="layui-table">' +
            '<tr>' +
            '<td><label>当前名称</label>：'+scName+'</td>' +
            '</tr>' +
            '<tr>' +
            '<td><label>更改名称</label>：' +
            '<input type="text" id="update-name" class="form-control" style="width:300px;display: inline-block;"/>' +
            '<button class="layui-btn name-update-btn">提交</button></td>' +
            '</tr>' +
            '</table>'
        });
    })
    $(document).on("click",".name-update-btn",function () {
        var scId = scIdName;
        var name = $.trim($("#update-name").val());
        if(name==''){
            layer.msg("系统分类名称不能为空", {icon: 5,anim: 6});
            return false;
        }
        var data = 'scId='+scId+"&name="+name
        $.ajax({
            url:'/admin/article/updateSCName',
            data:data,
            type:'post',
            success:function (resp) {
                if (resp.code==0) {
                    layer.close(index);
                    tr.children("td:nth-child(2)").text(name);
                } else {
                    layer.msg(resp.msg, {icon: 5,anim: 6});
                }
                return false;
            },
            error:function (resp) {
                layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
                return false;
            }
        })
    })

    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})