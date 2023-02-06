package ltd.newbee.mall.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    /**
     *
     * @return 跳转到商城首页
     */
    @GetMapping({"/index", "/", "/index.html"})
    public String index(){
        return "/mall/index";
    }
}
