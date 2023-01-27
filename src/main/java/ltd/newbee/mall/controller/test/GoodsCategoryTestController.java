package ltd.newbee.mall.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class GoodsCategoryTestController {



    /**
     * 测试分类展示
     * 1.默认先展示1级分类，所以需要到数据库获取1级分类列表
     * 2.跳转到分类展示测试页面
     *
     * @param request
     * @return
     */
    @GetMapping("/coupling-test")
    public String couplingTest(HttpServletRequest request,
                               @RequestParam("parentId") long parentId,
                               @RequestParam("categoryLevel") byte categoryLevel){
        request.setAttribute("path", "coupling-test");




        return "error/error_5xx";

//        return "admin/coupling-test";
    }
}
