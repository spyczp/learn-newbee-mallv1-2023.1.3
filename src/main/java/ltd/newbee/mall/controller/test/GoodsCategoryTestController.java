package ltd.newbee.mall.controller.test;

import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class GoodsCategoryTestController {

    @Resource
    private CategoryService categoryService;

    /**
     * 根据提交的父id，展示分类列表
     * 用于 当第一层/第二层选择分类后，第二层/第三层展示分类列表
     * @param parentId 分类的父id
     * @return 响应对象
     */
    @GetMapping("/categories/listForSelect")
    @ResponseBody
    public Object listForSelect(@RequestParam("parentId") long parentId){
        List<Category> categoryList = categoryService.queryCategoryListByParentId(parentId);
        return ResponseGenerator.genSuccessResponse(categoryList);
    }

    /**
     * 测试分类展示
     * 1.默认先展示1级分类，所以需要到数据库获取1级分类列表
     * 2.跳转到分类展示测试页面
     *
     * @param request
     * @return
     */
    @GetMapping("/categories/coupling-test")
    public String couplingTest(HttpServletRequest request,
                               @RequestParam("parentId") long parentId){

        request.setAttribute("path", "coupling-test");

        List<Category> categoryList = categoryService.queryCategoryListByParentId(parentId);
        request.setAttribute("levelOneCategoryList", categoryList);

        return "admin/coupling-test";
    }
}
