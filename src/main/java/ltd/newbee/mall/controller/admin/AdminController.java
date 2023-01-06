package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    /**
     *
     * 用户登出
     * 1.移除session中保存的用户信息
     * 2.移除session中保存的错误信息
     *
     * @param session 当前会话
     * @return 跳转到登录页面
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){

        //1.移除session中保存的用户信息
        session.removeAttribute(Constants.SESSION_LOGIN_USER);
        session.removeAttribute(Constants.SESSION_LOGIN_USER_ID);
        //2.移除session中保存的错误信息
        session.removeAttribute(Constants.ERROR_MSG);

        return "/admin/login";
    }

    /**
     *
     * 1.获取前端提交的新旧密码
     * 2.判断新旧密码是否为空
     * 3.通过session到数据库获取用户信息
     * 4.判断用户是否存在
     *   不存在，返回错误信息
     * 5.判断旧密码是否正确
     *   错误则返回错误提示信息
     * 6.旧密码正确，到数据库修改用户密码
     * 7.修改成功，返回成功信息；失败，返回失败信息
     *
     * @param originalPassword 旧密码
     * @param newPassword 新密码
     * @param session 当前会话
     * @return 响应信息对象
     */
    @PostMapping("/profile/passwordUpdate")
    @ResponseBody
    public Object passwordUpdate(@RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session){

        //创建响应信息的对象
        ResponseObj responseObj = new ResponseObj();

        //2.判断新旧密码是否为空
        if(!StringUtils.hasText(originalPassword) || !StringUtils.hasText(newPassword)){
            responseObj.setCode(1);
            responseObj.setMessage("新旧密码均不能为空");
            return responseObj;
        }

        //3.通过session到数据库获取用户信息
        Integer loginUserId = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID);
        AdminUser adminUser = adminUserService.queryAdminUserForProfile(loginUserId);
        //4.判断用户是否存在
        if(adminUser == null){
            responseObj.setCode(1);
            responseObj.setMessage("用户不存在");
            return responseObj;
        }
        //5.判断旧密码是否正确
        String originalPasswordMd5 = MD5Util.getMD5(originalPassword);
        if(!originalPasswordMd5.equals(adminUser.getLoginPassword())){
            responseObj.setCode(1);
            responseObj.setMessage("旧密码错误");
            return responseObj;
        }

        try{
            //封装数据
            HashMap<String, Object> map = new HashMap<>();
            String newPasswordMd5 = MD5Util.getMD5(newPassword);
            map.put("newPassword", newPasswordMd5);
            map.put("loginUserId", loginUserId);

            //访问业务层，传递数据，修改用户的密码
            //6.旧密码正确，到数据库修改用户密码
            int result = adminUserService.editPasswordAtProfile(map);
            if(result == 1){
                responseObj.setCode(0);
                responseObj.setMessage("修改密码成功");
                //密码修改成功后，需要移除session中保存的用户信息，不然当前session还是能访问其它页面。
                session.removeAttribute(Constants.SESSION_LOGIN_USER_ID);
                session.removeAttribute(Constants.SESSION_LOGIN_USER);
                session.removeAttribute(Constants.ERROR_MSG);
            }else{
                responseObj.setCode(1);
                responseObj.setMessage("修改密码失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj.setCode(1);
            responseObj.setMessage("修改密码失败");
        }

        return responseObj;
    }

    @PostMapping("/profile/nameUpdate")
    @ResponseBody
    public Object nameUpdate(@RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName,
                             HttpSession session){
        /**
         * 修改用户信息
         * 1.拿到用户信息:loginUserName, nickName, loginUserId(AdminUserId)
         * 2.判断用户信息是否为空
         * 3.用户信息不为空，则调业务层，修改数据库中的用户数据。
         *
         * * 这里还需判断用户信息是否合法，因为可能出现绕过前端直接访问后端接口的情况。后面再完善。
         */
        if(!StringUtils.hasText(loginUserName)){
            return "登录名称不能为空";
        }
        if(!StringUtils.hasText(nickName)){
            return "昵称不能为空";
        }
        //创建响应信息的对象
        ResponseObj responseObj = new ResponseObj();
        Integer loginUserId = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID);
        try{
           AdminUser adminUser = AdminUser.builder()
                            .adminUserId(loginUserId)
                            .loginUserName(loginUserName)
                            .nickName(nickName)
                            .build();
            int result = adminUserService.editUserInfoAtProfile(adminUser);
            if(result == 1){
                responseObj.setCode(0);
                responseObj.setMessage("修改用户信息成功");
            }else {
                responseObj.setCode(1);
                responseObj.setMessage("修改用户信息失败");
            }
            return responseObj;
        }catch (Exception e){
            e.printStackTrace();
            responseObj.setCode(1);
            responseObj.setMessage("修改用户信息失败");
        }
        return responseObj;
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, HttpSession session){
        /*
        * 1.从数据库获取用户信息
        * 2.把用户信息保存到请求域中，供前端profile页面展示使用:loginUserName, nickName
        * */
        Integer loginUserId = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID);
        AdminUser adminUser = adminUserService.queryAdminUserForProfile(loginUserId);
        if(adminUser == null){
            return "/admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());

        return "/admin/profile";
    }

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
