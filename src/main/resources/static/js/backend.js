$(function() {
    var href = location.href;
    if(href.search("article") != -1) {
        if(href.search("edit") != -1) {
            $("#type-list div:first-child a:nth-child(2)").css({"background-color":"red","color":"white"})
        } else if (href.search("list") != -1) {
            $("#type-list div:first-child a:nth-child(2)").css({"background-color":"red","color":"white"})
        }
    } else if(href.search("comment") != -1) {
        $("#type-list div:first-child a:nth-child(3)").css({"background-color":"red","color":"white"})
    } else if(href.search("category") != -1) {
        $("#type-list div:first-child a:nth-child(4)").css({"background-color":"red","color":"white"})
    }else if(href.search("forum")!=-1){
        if(href.search("list") != -1) {
            $("#type-list div:nth-child(2) a:nth-child(2)").css({"background-color":"red","color":"white"})
        } else if (href.search("reply") != -1) {
            $("#type-list div:nth-child(2) a:nth-child(3)").css({"background-color":"red","color":"white"})
        }
    } else if(href.search("user")!=-1){
        $("#type-list div:nth-child(3) a:nth-child(3)").css({"background-color":"red","color":"white"})
    }
})