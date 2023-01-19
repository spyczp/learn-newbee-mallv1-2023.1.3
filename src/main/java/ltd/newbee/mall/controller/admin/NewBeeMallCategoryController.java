package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.service.impl.CategoryService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class NewBeeMallCategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 展示分类列表，用到了前端分页插件
     * 1.拿到categoryLevel、parentId
     *      作为获取分类列表的查询条件
     *      1.根据层级判断
     *          判断层级是否为空
     *          若层级为1，则直接到数据库查询第一层数据，无需判断父id
     *          若层级为2，判断父id是否为空。根据层级和父id查询第二层数据
     *          若层级为3，判断父id是否为空。根据层级和父id查询第三层数据
     *      2.分页数据
     *
     * @param categoryLevel 分类层级
     * @param parentId 分类父id
     * @return 响应对象，包含分类列表和分页数据
     */
    @GetMapping("/categories/list")
    @ResponseBody
    public Object showCategoryList(@RequestParam("categoryLevel") Integer categoryLevel,
                                   @RequestParam("parentId") Integer parentId,
                                   @RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize){

        if(categoryLevel == null ||
                parentId == null ||
                pageNum == null ||
                pageSize == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }
        if(categoryLevel < 1 || categoryLevel > 3){
            return ResponseGenerator.genFailResponse("参数不合法");
        }
        Map<String, Object> map = new HashMap<>();
        Integer startIndex = (pageNum - 1) * pageSize;
        map.put("categoryLevel", categoryLevel);
        map.put("pageNum", pageNum);
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        map.put("parentId", null);
        if(categoryLevel != 1){
            map.put("parentId", parentId);
        }

        PageResult pageResult = categoryService.queryCategoryListForPagination(map);
        return ResponseGenerator.genSuccessResponse(pageResult);
    }


    /**
     * 跳转到分类页面
     * 1.保存数据到请求域中，供前端使用
     * @param parentId 分类的父id
     * @param categoryLevel 分类所处层级
     * @param backParentId 分类的爷id
     * @param request
     * @return 分类页面
     */
    @GetMapping("/categories")
    public String categoriesPage(@RequestParam("parentId") Integer parentId,
                                 @RequestParam("categoryLevel") Integer categoryLevel,
                                 @RequestParam("backParentId") Integer backParentId,
                                 @RequestParam("oldParentId") Integer oldParentId,
                                 HttpServletRequest request){

        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("categoryLevel", categoryLevel);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("oldParentId", oldParentId);

        return "/admin/newbee_mall_category";
    }
}
