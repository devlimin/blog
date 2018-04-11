package com.limin.blog.util;

import com.limin.blog.vo.Response;

public class ResponseUtil {
    public static Response success(Object data) {
        Response response = new Response();
        response.setCode(0);
        response.setMsg("success");
        response.setData(data);
        return response;
    }
    public static Response success() {
        return success(null);
    }
    public static Response error(Integer code, String msg) {
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
