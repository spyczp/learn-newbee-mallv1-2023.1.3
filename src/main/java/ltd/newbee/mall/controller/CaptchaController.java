package ltd.newbee.mall.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import ltd.newbee.mall.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;


@Controller
@RequestMapping("/common")
public class CaptchaController {

    /**
     * 判断用户输入的验证码和实际验证码是否相等（核实用户的验证码）
     * @param code
     * @param session
     * @return
     */
    @GetMapping("/verify")
    @ResponseBody
    public String verify(@RequestParam("code") String code, HttpSession session){
        /*
        * 1.判断code是否为空
        * 2.判断code是否等于实际的验证码
        * 3.不相等返回验证失败
        * 4.相等返回验证成功
        * */
        if(!StringUtils.hasLength(code)){
            return "验证码不能为空";
        }
        String verifyCode = (String) session.getAttribute(Constants.VERIFY_CODE);
        System.out.println("verifyCode: " + verifyCode);
        System.out.println("code: " + code);
        if(!StringUtils.hasLength(verifyCode) || !code.toLowerCase().equals(verifyCode)){
            return "验证码错误";
        }
        return "验证成功";
    }

    /**
     * 生成验证码，并输出验证码图片流
     * @param request
     * @param response
     * @throws IOException
     * @throws FontFormatException
     */
    @GetMapping("/captcha")
    public void defaultCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, FontFormatException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/gif");

        // 三个参数分别为宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(75, 30, 4);

        // 设置类型为数字和字母混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 设置字体
        captcha.setFont(Captcha.FONT_9);

        // 验证码存入session
        request.getSession().setAttribute(Constants.VERIFY_CODE, captcha.text().toLowerCase());

        // 输出图片流，供前端展示验证码
        captcha.out(response.getOutputStream());
    }
}
