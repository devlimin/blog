$(function () {
    $(document).on("click","#search-btn",function() {
        var keywords = $.trim($("#search-input").val());
        if (keywords=="") {
            return false;
            layer.msg("系统出现问题，请联系管理员", {icon: 5,anim: 6});
        }
        window.location.href="/search?keywords="+keywords;
    })
    Date.prototype.format = function () {
        return this.getFullYear() + "." + (this.getMonth() + 1) + "." + this.getDate() + " " + this.getHours() + ":" + this.getMinutes() + ":"+this.getSeconds();
    }
})