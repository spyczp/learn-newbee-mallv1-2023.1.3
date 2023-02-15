package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.IndexConfigTypeEnum;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.service.MallIndexConfigService;
import ltd.newbee.mall.vo.MallIndexConfigGoodsVO;
import ltd.newbee.mall.vo.NewBeeMallIndexCarouselVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static ltd.newbee.mall.common.Constants.*;

@Controller
public class IndexController {

    @Resource
    private CarouselService carouselService;

    @Resource
    private MallIndexConfigService mallIndexConfigService;

    /**
     * 1.获取轮播图列表，展示轮播图
     * 2.获取热销、新品、推荐商品列表，用于展示
     *
     * @return 跳转到商城首页
     */
    @GetMapping({"/index", "/", "/index.html"})
    public String index(HttpServletRequest request){

        List<NewBeeMallIndexCarouselVO> carouselVOList = carouselService.queryCarouselListByCount(INDEX_CAROUSEL_COUNT);

        //这些都是根据 商城配置信息的goodsId 查询到的 商品信息
        //所以这些商品信息和商城配置信息一一对应
        /**
         * 商城配置信息 --goodsId--> 商品信息
         * 商城配置信息 --goodsId--> 商品信息
         * 商城配置信息 --goodsId--> 商品信息
         * 商城配置信息 --goodsId--> 商品信息
         * 商城配置信息 --goodsId--> 商品信息
         */

        List<MallIndexConfigGoodsVO> goodsHotList = mallIndexConfigService.queryGoodsForIndexShow((byte) IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsVO> goodsNewList = mallIndexConfigService.queryGoodsForIndexShow((byte) IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsVO> goodsRecommendList = mallIndexConfigService.queryGoodsForIndexShow((byte) IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), INDEX_GOODS_RECOMMEND_NUMBER);

        request.setAttribute("carouselVOList", carouselVOList);
        request.setAttribute("goodsHotList", goodsHotList);
        request.setAttribute("goodsNewList", goodsNewList);
        request.setAttribute("goodsRecommendList", goodsRecommendList);

        return "/mall/index";
    }



}
