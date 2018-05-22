function isPositiveInteger(num){//是否为正整数
    if ($.trim(num)!=''&&(!(/(^[1-9]\d*$)/.test($.trim(num))))) {
        return false;
    }
    return true;
}