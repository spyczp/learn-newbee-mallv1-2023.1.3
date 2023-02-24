package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.service.GoodsInfoService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private GoodsInfoService goodsInfoService;

    /**
     * 跳转到商品详情页面
     * @param goodsId 商品id
     * @return 商品详情页面
     */
    @GetMapping("/detail/{goodsId}")
    public String goodsDetail(@PathVariable("goodsId") Long goodsId, HttpServletRequest request){
        if(ObjectUtils.isEmpty(goodsId)){
            return "error/500";
        }

        GoodsInfo goodsInfo = goodsInfoService.queryGoodsInfoByGoodsId(goodsId);
        if(ObjectUtils.isEmpty(goodsInfo)){
            return "error/500";
        }

        request.setAttribute("goodsDetail", goodsInfo);

        return "mall/detail";
    }

    /**
     * 在搜索结果页面展示商品数据
     * 1.验证前端提交的参数的合法性
     * 2.访问数据库，查询商品数据。封装成分页数据
     * 3.返回响应数据给前端
     * @param {
     *        pageNum 当前页码
     *        pageSize 每页显示条数
     *        column 按哪列字段排序
     *        order 顺序还是倒序
     *        keyword 关键字
     *        categoryId 分类id
     *           }
     * @return 响应对象，包含分页数据和列表
     */
    @GetMapping("/showGoodsInfoList")
    @ResponseBody
    public Object showGoodsInfoList(@RequestParam("pageNum") Integer pageNum,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("column") String column,
                                    @RequestParam("order") String order,
                                    String goodsName,
                                    Long categoryId){

        if(ObjectUtils.isEmpty(pageNum) ||
           ObjectUtils.isEmpty(pageSize) ||
           ObjectUtils.isEmpty(column) ||
           ObjectUtils.isEmpty(order) ||
           pageNum <= 0 ||
           pageSize < 0){
            return ResponseGenerator.genFailResponse("分页参数异常");
        }

        if(ObjectUtils.isEmpty(goodsName) && ObjectUtils.isEmpty(categoryId)){
            return ResponseGenerator.genFailResponse("关键字和分类均未确定");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("column", column);
        map.put("order", order);
        map.put("goodsName", goodsName);
        map.put("categoryId", categoryId);

        PageResult pageResult = goodsInfoService.searchGoodsInfoListForMall(map);

        return ResponseGenerator.genSuccessResponse(pageResult);
    }

    /**
     * 跳转到 商品搜索结果 页面
     * 1.因为搜索页面需要显示用户输入的关键字或者分类，所以把这些参数放到请求域中。
     * 2.分类特殊一点，如果用户点击的是子分类，还需要显示其父分类甚至爷分类。
     * 3.所以，需要到数据库查询父分类的信息并保存到请求域中。
     * @param keyword 关键字
     * @param categoryId 分类
     * @return 商品搜索结果 页面
     */
    @GetMapping("/search")
    public String searchResultPage(String keyword,
                                   Long categoryId,
                                   HttpServletRequest request){

        if(StringUtils.hasText(keyword)){
            //把关键字放到请求域，供 搜索结果页面 获取
            request.setAttribute("keyword", keyword);
        }
        if(!ObjectUtils.isEmpty(categoryId)){
            Category currentCategory = categoryService.queryCategoryByCategoryId(categoryId);
            request.setAttribute("currentCategory", currentCategory);
            if(currentCategory.getParentId() != 0){
                Category parentCategory = categoryService.queryCategoryByCategoryId(currentCategory.getParentId());
                request.setAttribute("parentCategory", parentCategory);
                if(parentCategory.getParentId() != 0){
                    Category topCategory = categoryService.queryCategoryByCategoryId(parentCategory.getParentId());
                    request.setAttribute("topCategory", topCategory);
                }
            }
        }

        return "/mall/search";
    }
}
