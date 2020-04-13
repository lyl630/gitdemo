package cn.pro.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
public class SsoCookieWrapperInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        StringBuffer ssoCookies = new StringBuffer();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            ssoCookies
                    .append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append(";");
        }
        ssoCookies.deleteCharAt(ssoCookies.length() - 1);
        request.setAttribute("ssoCookies", ssoCookies.toString());
        return true;
    }
}