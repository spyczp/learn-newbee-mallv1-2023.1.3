package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 删除一条分类信息，实际是到数据库把该条信息的is_deleted设置为1
     * 1.拿到分类id组成的数组ids
     *      判断ids是否为空
     * 2.把ids提交到数据库，修改指定id的数据的is_deleted设置为1
     * 3.返回响应对象给前端
     * @param ids 前端提交的需要删除的分类的id组成的数组
     * @return 响应对象
     */
    @PostMapping("/categories/delete")
    @ResponseBody
    public Object deleteACategory(@RequestBody Integer[] ids){
        if(ids == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        ResponseObj responseObj = null;
        try{
            int result = categoryService.editSomeIsDeletedToOne(ids);
            if(result == ids.length){
                //代表成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                responseObj = ResponseGenerator.genFailResponse("删除分类信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("删除分类信息失败");
        }
        return responseObj;
    }

    /**
     * 修改分类信息
     * 1.获取前端提供的分类信息：
     *     "categoryId": id,
     *     "categoryName": categoryName,
     *     "categoryLevel": categoryLevel,
     *     "parentId": parentId,
     *     "categoryRank": categoryRank
     * 2.判断数据是否为空
     * 3.封装其它数据：
     *      update_time,
     *      update_user
     * 4.访问业务层，到数据库中修改指定的数据
     * 5.返回响应对象
     * @param category 把前端提交的分类数据封装到实体类中
     * @return 响应对象
     */
    @PostMapping("/categories/update")
    @ResponseBody
    public Object editACategory(@RequestBody Category category, HttpSession session){
        if(category.getCategoryId() == null
            || !StringUtils.hasText(category.getCategoryName())
            || category.getCategoryLevel() == null
            || category.getParentId() == null
            || category.getCategoryRank() == null){

            return ResponseGenerator.genFailResponse("参数异常");
        }

        category.setUpdateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        category.setUpdateTime(new Date());

        ResponseObj responseObj = null;
        try{
            int result = categoryService.editACategory(category);
            if(result == 1){
                //代表成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                responseObj = ResponseGenerator.genFailResponse("修改分类信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("修改分类信息失败");
        }

        return responseObj;
    }

    /**
     * 新增分类信息
     * 1.获取前端提交的数据：categoryName、categoryLevel、parentId、categoryRank（需要判断数据是否为空）
     * 2.封装其它数据：isDeleted、createTime、createUser
     * 3.访问业务层，向数据库中插入新的分类数据
     * 4.返回响应对象
     * @param category 把前端提交的分类数据封装到实体类中
     * @return 响应对象
     */
    @PostMapping("/categories/save")
    @ResponseBody
    public Object createACategory(@RequestBody Category category, HttpSession session){
        if(!StringUtils.hasText(category.getCategoryName())
            || category.getCategoryLevel() == null
            || category.getParentId() == null
            || category.getCategoryRank() == null){

            return ResponseGenerator.genFailResponse("参数异常");
        }
        byte num = 0;
        category.setIsDeleted(num);
        //数据库保存的 创建时间 晚了8小时
        category.setCreateTime(new Date());
        category.setCreateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        ResponseObj responseObj = null;
        try {
            int result = categoryService.createACategory(category);
            if(result == 1){
                //代表新增成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                responseObj = ResponseGenerator.genFailResponse("新增分类信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("新增分类信息失败");
        }
        return responseObj;
    }

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
