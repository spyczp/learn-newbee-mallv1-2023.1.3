package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session){
        /*
        * 1.判断用户输入的验证码是否为空
        *   为空，跳转登录页面，返回错误信息
        *   不为空，向下执行。
        * 2.判断用户输入的用户名和密码是否为空
        *   为空，跳转登录页面，返回错误信息
        *   不为空，向下执行。
        * 以上均成立
        *   3.判断验证码是否正确。
        *       正确则向下执行。
        *       不正确则跳转登录页面。
        *   4.判断用户信息是否存在。
        *       存在则建立session，保存用户信息。跳转到首页。
        *       不存在则跳转登录页面。
        * */
        if(!StringUtils.hasText(verifyCode)){
            session.setAttribute(Constants.ERROR_MSG, "验证码不能为空");
            return "/admin/login";
        }

        if(!StringUtils.hasText(userName) || !StringUtils.hasText(password)){
            session.setAttribute(Constants.ERROR_MSG, "用户名或密码不能为空");
            return "/admin/login";
        }

        String captChaCode = (String) session.getAttribute(Constants.VERIFY_CODE);
        System.out.println("captChaCode: " + captChaCode);
        System.out.println("verifyCode: " + verifyCode);
        if(!StringUtils.hasLength(captChaCode) || !verifyCode.toLowerCase().equals(captChaCode)){
            session.setAttribute(Constants.ERROR_MSG, "验证码错误");
            return "/admin/login";
        }

        AdminUser adminUser = adminUserService.queryAdminUserForLogin(userName, password);
        if(adminUser != null){
            session.setAttribute(Constants.SESSION_LOGIN_USER, adminUser.getNickName());
            session.setAttribute(Constants.SESSION_LOGIN_USER_ID, adminUser.getAdminUserId());
            return "redirect:/admin/index";
        }else {
            session.setAttribute(Constants.ERROR_MSG, "登录失败");
            return "/admin/login";
        }
    }

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping({"", "/", "/index", "/index.html"})
    public String indexAll(HttpServletRequest request){
        request.setAttribute("path", "index");
        return "admin/index";
    }
}
