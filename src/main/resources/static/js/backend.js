$(function() {
    var href = location.href;
    if(href.search("article") != -1) {
        if(href.search("edit") != -1) {
            $("#type-list div:first-child a:nth-child(2)").css({"background-color":"red","color":"white"})
        } else if (href.search("list") != -1) {
            $("#type-list div:first-child a:nth-child(3)").css({"background-color":"red","color":"white"})
        }
    } else if(href.search("comment") != -1) {
        $("#type-list div:first-child a:nth-child(4)").css({"background-color":"red","color":"white"})
    } else if(href.search("category") != -1) {
        $("#type-list div:first-child a:nth-child(5)").css({"background-color":"red","color":"white"})
    }
})