package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.vo.NewBeeMallIndexCarouselVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static ltd.newbee.mall.common.Constants.INDEX_CAROUSEL_COUNT;

@Controller
public class IndexController {

    @Resource
    private CarouselService carouselService;

    /**
     * 1.获取轮播图列表，展示轮播图
     *
     * @return 跳转到商城首页
     */
    @GetMapping({"/index", "/", "/index.html"})
    public String index(HttpServletRequest request){

        List<NewBeeMallIndexCarouselVO> carouselVOList = carouselService.queryCarouselListByCount(INDEX_CAROUSEL_COUNT);

        request.setAttribute("carouselVOList", carouselVOList);

        return "/mall/index";
    }



}
