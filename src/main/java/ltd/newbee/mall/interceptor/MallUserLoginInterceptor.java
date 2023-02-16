package ltd.newbee.mall.interceptor;

import ltd.newbee.mall.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MallUserLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(ObjectUtils.isEmpty(request.getSession().getAttribute(Constants.MALL_USER_LOGIN_ID))){
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }else{
            return true;
        }
    }
}
