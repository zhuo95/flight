package com.zz.flight.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class cookieUtil {

    /**
     *@Description:addCookie,设置cookie
     *@Param:[response, name, value, maxAge]
     *@Return:void
     *@Author:zz
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     *@Description:getCookieByName，获取cookie
     *@Param:[request, name]
     *@Return:javax.servlet.http.Cookie
     *@Author:zz
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name){
        Map<String,Cookie> cookieMap = ReadCookieMap(request);
        if(cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie;
        }else{
            return null;
        }
    }


    /**
     *@Description:ReadCookieMap，将cookie封装到Map里面
     *@Param:[request]
     *@Return:java.util.Map<java.lang.String,javax.servlet.http.Cookie>
     *@Author:zz
     */
    private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
