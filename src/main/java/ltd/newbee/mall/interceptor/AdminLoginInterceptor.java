package ltd.newbee.mall.interceptor;

import ltd.newbee.mall.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
        * 1.判断请求uri是否是/admin
        * 2.判断session中是否有登录的用户信息
        *   当访问/admin，且session中没有用户信息时，拦截，跳转到登录页面
        * 3.其它情况放行:
        *   访问/admin，且session中有用户信息
        *   访问非/admin资源，session中没有用户信息(可以丢到另一个拦截器中处理)
        *   访问非/admin资源，session中有用户信息
        * */
        System.out.println("进入管理登录拦截器");
        String requestURI = request.getRequestURI();
        if(requestURI.startsWith("/admin") && null == request.getSession().getAttribute(Constants.SESSION_LOGIN_USER)){
            request.setAttribute(Constants.ERROR_MSG, "请先登录");
            response.sendRedirect(request.getContextPath() + "/admin/login");
            System.out.println("未登录，拦截成功...");
            return false;
        }else{
            request.removeAttribute(Constants.ERROR_MSG);
            System.out.println("管理登录拦截器放行");
            return true;
        }
    }
}
