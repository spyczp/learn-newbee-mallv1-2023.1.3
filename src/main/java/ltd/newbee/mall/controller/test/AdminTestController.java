package ltd.newbee.mall.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminTestController {

    @GetMapping("/error500")
    public String error500(){
        return "error/500";
    }
}
