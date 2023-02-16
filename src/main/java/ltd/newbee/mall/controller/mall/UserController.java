package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ResponseMsgEnum;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     * 1.验证前端提交的参数的合法性
     * 2.检验验证码是否正确
     * 3.验证通过，把用户信息提交到业务层进一步验证处理
     * 4.返回响应对象给前端，包含登录结果
     * @param loginName 用户名
     * @param password 密码
     * @param verifyCode 验证码
     * @param session
     * @return 响应对象
     */
    @PostMapping("/login")
    @ResponseBody
    public Object userLogin(@RequestParam("loginName") String loginName,
                            @RequestParam("password") String password,
                            @RequestParam("verifyCode") String verifyCode,
                            HttpSession session){
        //1.验证前端提交的参数的合法性
        if(!StringUtils.hasText(loginName) ||
            !StringUtils.hasText(password) ||
            !StringUtils.hasText(verifyCode)){

            return ResponseGenerator.genFailResponse("参数异常");
        }

        //2.检验验证码是否正确
        String actualCode = (String) session.getAttribute(Constants.VERIFY_CODE);
        if(!StringUtils.hasText(actualCode) || !verifyCode.toLowerCase().equals(actualCode)){
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.VERIFY_CODE_WRONG.getMessage());
        }

        //3.验证通过，把用户信息提交到业务层进一步验证处理
        //均验证通过，把用户信息保存到session域中
        /*
        *  用户登录验证
         * 1.根据用户名查询用户是否存在
         * 2.若存在，判断密码是否正确
         * 3.密码正确，则把用户信息保存到session域中
         * 4.若正确，返回用户信息
        * */
        User user = userService.userLogin(loginName);
        if(user == null){
            //用户名不存在
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.USER_LOGIN_NOT_EXIST.getMessage());
        }else{
            //2.若存在，判断密码：不正确或者数据库用户信息中的密码为空，代表密码验证失败
            if(!MD5Util.getMD5(password).equals(user.getPasswordMd5()) || !StringUtils.hasText(user.getPasswordMd5())){
                //密码错误
                return ResponseGenerator.genFailResponse(ResponseMsgEnum.USER_LOGIN_PASSWORD_WRONG.getMessage());
            }else{
                //密码正确，则把用户信息保存到session域中
                session.setAttribute(Constants.MALL_USER_LOGIN_NAME, loginName);
                session.setAttribute(Constants.MALL_USER_NICK_NAME, user.getNickName());
                session.setAttribute(Constants.MALL_USER_LOGIN_ID, user.getUserId());
                //删除session中的verifyCode
                session.removeAttribute(Constants.VERIFY_CODE);
                return ResponseGenerator.genSuccessResponse();
            }
        }
    }

    /**
     * 注册用户：前端提交用户信息
     * 1.验证前端提交的参数合法性
     * 2.验证用户输入的验证码是否等于显示的验证码
     * 3.验证通过，则访问业务层，提交用户数据，往数据库插入数据
     * 4.返回响应信息给前端
     * @param loginName 用户名称
     * @param password 密码
     * @param verifyCode 验证码
     * @return 响应对象，包含注册结果
     */
    @PostMapping("/register")
    @ResponseBody
    public Object userRegister(@RequestParam("loginName") String loginName,
                               @RequestParam("password") String password,
                               @RequestParam("verifyCode") String verifyCode,
                               HttpSession session){

        //1.验证前端提交的参数合法性
        if(!StringUtils.hasText(loginName) ||
           !StringUtils.hasText(password) ||
           !StringUtils.hasText(verifyCode)){

            return ResponseGenerator.genFailResponse("参数异常");
        }

        //2.验证用户输入的验证码是否等于显示的验证码
        String actualCode = (String) session.getAttribute(Constants.VERIFY_CODE);
        if(!StringUtils.hasText(actualCode) || !verifyCode.toLowerCase().equals(actualCode)){
            return ResponseGenerator.genFailResponse("验证码错误");
        }

        //3.验证通过，则访问业务层，提交用户数据，往数据库插入数据
        try{
            return userService.addAUser(loginName, password);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.USER_REGISTER_ERROR.getMessage());
        }
    }

    /**
     *
     * @return 跳转到 商城用户登录页面
     */
    @GetMapping({"/login", "login.html"})
    public String loginPage(){
        return "/mall/login";
    }

    /**
     *
     * @return 跳转到 商城用户注册页面
     */
    @GetMapping({"/register", "register.html"})
    public String registerPage(){
        return "/mall/register";
    }
}
