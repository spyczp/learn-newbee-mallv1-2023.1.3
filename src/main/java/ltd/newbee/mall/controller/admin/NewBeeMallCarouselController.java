package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class NewBeeMallCarouselController {

    @Resource
    private CarouselService carouselService;

    /**
     * 展示轮播图列表
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    @GetMapping("/carousels/list")
    public Object list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize){
        //判断前端提交的数据是否为空
        if(pageNum == null || pageSize == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }
        //判断当前页码和每页显示条数是否合法
        if(pageNum <= 0 || pageSize <= 0){
            return ResponseGenerator.genFailResponse("参数不合法");
        }

        //调佣业务层，返回分页数据
        PageResult pageResult = carouselService.queryCarouseListForPagination(pageNum, pageSize);
        return ResponseGenerator.genSuccessResponse(pageResult);
    }

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.setAttribute("path", "newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }
}
