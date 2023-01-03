package ltd.newbee.mall.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping({"", "/", "/index", "/index.html"})
    public String indexAll(HttpServletRequest request){
        request.setAttribute("path", "index");
        return "admin/index";
    }
}
