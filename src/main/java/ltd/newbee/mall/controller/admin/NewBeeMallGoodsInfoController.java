package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.service.GoodsInfoService;
import ltd.newbee.mall.util.ImageData;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.apache.ibatis.reflection.ArrayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static ltd.newbee.mall.common.Constants.FILE_UPLOAD_DIC;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsInfoController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private GoodsInfoService goodsInfoService;

    /**
     * 把前端提交的指定商品id的商品的销售状态改为上架
     * 1.拿到商品id组成的列表
     * 2.验证数据合法性
     * 3.访问业务层，到数据库修改销售状态
     * 4.返回响应数据给前端
     * @param ids 商品id组成的数组
     * @return 处理结果
     */
    @PutMapping("/goods/status/{status}")
    @ResponseBody
    public Object putGoodsSellStatus(@RequestBody Long[] ids,
                                     @PathVariable("status") Integer status){
        if(ObjectUtils.isEmpty(ids) || ObjectUtils.isEmpty(status)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int result = goodsInfoService.changeGoodsSellStatusByIdsAndStateNum(ids, status);
            if(result > 0){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("修改商品上下架状态失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("修改商品上下架状态失败");
        }
    }

    /**
     * 展示商品信息列表
     * 1.获取前端提交的数据：页码、每页显示条数
     * 2.访问业务层，获取分页需要的数据
     * 3.创建响应数据，返回给前端
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 响应对象，包含分页插件使用的数据
     */
    @GetMapping("/goods/list")
    @ResponseBody
    public Object showGoodsList(@RequestParam("pageNum") Integer pageNum,
                                @RequestParam("pageSize") Integer pageSize){
        if(ObjectUtils.isEmpty(pageNum) || ObjectUtils.isEmpty(pageSize)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        PageResult pageResult = goodsInfoService.queryGoodsInfoListForPagination(pageNum, pageSize);

        return ResponseGenerator.genSuccessResponse(pageResult);
    }

    @GetMapping("/goods")
    public String toGoodsPage(HttpServletRequest request){
        request.setAttribute("path", "newbee_mall_goods");

        return "/admin/newbee_mall_goods";
    }

    /**
     * 前端点击保存按钮，保存修改之后的商品信息到数据库
     * 1.验证前端提交的数据的合法性（目前只验证是否为空）
     * 2.调用业务层，往数据库中插入数据
     * 3.给前端响应处理结果
     * @param goodsInfo 封装商品信息数据
     * @return 响应对象
     */
    @PostMapping("/goods/update")
    @ResponseBody
    public Object editAGoodsInfo(@RequestBody GoodsInfo goodsInfo,
                                 HttpSession session){

        if(!StringUtils.hasText(goodsInfo.getGoodsName()) ||
                !StringUtils.hasText(goodsInfo.getGoodsIntro()) ||
                !StringUtils.hasText(goodsInfo.getGoodsCoverImg()) ||
                !StringUtils.hasText(goodsInfo.getGoodsDetailContent()) ||
                !StringUtils.hasText(goodsInfo.getTag()) ||
                ObjectUtils.isEmpty(goodsInfo.getGoodsId()) ||
                ObjectUtils.isEmpty(goodsInfo.getGoodsCategoryId()) ||
                ObjectUtils.isEmpty(goodsInfo.getOriginalPrice()) ||
                ObjectUtils.isEmpty(goodsInfo.getSellingPrice()) ||
                ObjectUtils.isEmpty(goodsInfo.getStockNum()) ||
                ObjectUtils.isEmpty(goodsInfo.getGoodsSellStatus())){

            return ResponseGenerator.genFailResponse("参数异常");
        }

        goodsInfo.setUpdateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        goodsInfo.setUpdateTime(new Date());

        try{
            int result = goodsInfoService.editAGoodsInfo(goodsInfo);
            if(result == 1){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("修改商品信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("修改商品信息失败");
        }
    }

    /**
     * 1.获取商品id，验证合法性
     * 2.从数据库获取商品数据
     * 3.判断商品的分类id
     *      分类id为0，则说明商品没有分类，则不需要查询商品的1,2,3级分类
     *      分类id大于0，则说明商品已配置分类，则需要查询商品的1,2,3级分类
     * 4.分类id大于0时，查询1,2,3级分类列表。分类id为0时，只需查询1级分类列表
     *      查询3级分类列表：根据商品所属的3级分类，父id查询3级分类列表
     *      查询2级分类列表：根据商品所属的3级分类，父id确认2级分类，根据2级分类id查询1级分类父id，然后再根据1级分类id查询2级分类列表
     *      查询1级分类列表：根据层级查询1级分类列表
     * 5.把商品对应的各级分类id、各级分类列表数据、商品信息数据 保存到请求域，返回给前端
     * @param goodsId 商品id
     * @return 跳转到商品编辑页面
     */
    @GetMapping("/goods/edit/{goodsId}")
    public String toGoodsEditPage(@PathVariable("goodsId") Long goodsId,
                                  HttpServletRequest request){
        if(ObjectUtils.isEmpty(goodsId)){
            return "error/400";
        }

        GoodsInfo goodsInfo = goodsInfoService.queryGoodsInfoByGoodsId(goodsId);

        request.setAttribute("path", "goods-edit");
        request.setAttribute("goods", goodsInfo);

        if(goodsInfo.getGoodsCategoryId() > 0){
            //查询当前商品的分类信息
            Category currentLevelThreeCategory = categoryService.queryCategoryByCategoryId(goodsInfo.getGoodsCategoryId());
            //获得分类的父分类id
            Long parentId = currentLevelThreeCategory.getParentId();
            //父分类id既是2层分类的id
            //根据这个父分类id查询3级分类列表，即父分类id为该id的所有3级分类
            //3级分类列表
            List<Category> levelThreeCategoryList = categoryService.queryCategoryListByParentId(parentId);
            //查询id为parentId的2级分类
            Category currentLevelTwoCategory = categoryService.queryCategoryByCategoryId(parentId);
            //2级分类的父id既是1级分类
            Long levelTwoParentId = currentLevelTwoCategory.getParentId();
            //查询父id为levelTwoParentId的所有2级分类
            List<Category> levelTwoCategoryList = categoryService.queryCategoryListByParentId(levelTwoParentId);
            //查询1级分类信息
            Category currentLevelOneCategory = categoryService.queryCategoryByCategoryId(levelTwoParentId);
            Long levelOneParentId = currentLevelOneCategory.getParentId();
            //查询所有1级分类
            List<Category> levelOneCategoryList = categoryService.queryCategoryListByParentId(levelOneParentId);
            //封装数据到请求域中
            request.setAttribute("currentLevelThreeCategory", currentLevelThreeCategory);
            request.setAttribute("currentLevelTwoCategory", currentLevelTwoCategory);
            request.setAttribute("currentLevelOneCategory", currentLevelOneCategory);
            request.setAttribute("levelThreeCategoryList", levelThreeCategoryList);
            request.setAttribute("levelTwoCategoryList", levelTwoCategoryList);
            request.setAttribute("levelOneCategoryList", levelOneCategoryList);

            //跳转到商品编辑页面
            return "admin/newbee_mall_goods_edit";
        }

        if(goodsInfo.getGoodsCategoryId() == 0){
            //数据库有些商品的分类id为0，原作者的意思是这些数据是没有添加分类的商品数据。
            //但正常来说商品创建时必须添加分类，前端新建商品的页面，也对分类是否为空进行了验证。
            //所以，这里为了在修改这些数据时展示1级分类，直接采用父id为0作为查询条件。
            List<Category> levelOneCategoryList = categoryService.queryCategoryListByParentId(0L);
            request.setAttribute("levelOneCategoryList", levelOneCategoryList);

            return "admin/newbee_mall_goods_edit";
        }

        return "error/500";

    }

    /**
     * 在商品编辑页面，点击保存按钮，调用本方法，在数据库新增一条商品信息数据。
     * 1.拿到前端提交的商品数据。
     * 2.验证商品数据的合法性（目前只验证商品信息是否为空）。
     * 3.数据没问题，则调用业务层，往数据库新增一条商品信息数据。
     * 4.生成返回信息对象给前端。
     * @param goodsInfo 商品信息数据
     * @return 返回信息
     */
    @PostMapping("/goods/save")
    @ResponseBody
    public Object createAGoodsInfo(@RequestBody GoodsInfo goodsInfo,
                                   HttpSession session){

        if(!StringUtils.hasText(goodsInfo.getGoodsName()) ||
           !StringUtils.hasText(goodsInfo.getGoodsIntro()) ||
           !StringUtils.hasText(goodsInfo.getGoodsCoverImg()) ||
           !StringUtils.hasText(goodsInfo.getGoodsDetailContent()) ||
           !StringUtils.hasText(goodsInfo.getTag()) ||
           ObjectUtils.isEmpty(goodsInfo.getGoodsCategoryId()) ||
           ObjectUtils.isEmpty(goodsInfo.getOriginalPrice()) ||
           ObjectUtils.isEmpty(goodsInfo.getSellingPrice()) ||
           ObjectUtils.isEmpty(goodsInfo.getStockNum()) ||
           ObjectUtils.isEmpty(goodsInfo.getGoodsSellStatus())){

            return ResponseGenerator.genFailResponse("参数异常");
        }

        goodsInfo.setCreateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        goodsInfo.setCreateTime(new Date());

        try{
            int result = goodsInfoService.createAGoodsInfo(goodsInfo);
            if(result == 1){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("保存商品信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("保存商品信息失败");
        }
    }

    @PostMapping("/goods/uploadImage")
    @ResponseBody
    public Object uploadImage(@RequestParam("file") MultipartFile file,
                              HttpServletRequest request){

        //1.判断前端上传的文件是否为空
        if(file.isEmpty()){
            return ResponseGenerator.genFailResponse();
        }
        //① 先获取文件后缀名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //② 生成 日期格式+随机数+文件后缀名 文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();

        try {
            //3.保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_DIC, newFileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseGenerator.genFailResponse();
        }

        ImageData imageData = new ImageData();
        imageData.setUrl(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/upload/" + newFileName);

        return ResponseGenerator.genSuccessResponse(imageData);
    }


    /**
     * 根据提交的父id，展示分类列表
     * 用于 当第一层/第二层选择分类后，第二层/第三层展示分类列表
     * @param parentId 分类的父id
     * @return 响应对象
     */
    @GetMapping("/goods/listForSelect")
    @ResponseBody
    public Object listForSelect(@RequestParam("parentId") long parentId){
        List<Category> categoryList = categoryService.queryCategoryListByParentId(parentId);
        return ResponseGenerator.genSuccessResponse(categoryList);
    }

    /**
     * 1.商品编辑页面需要选择商品的分类。所以需要从数据库获取第一层分类列表。
     * 2.跳转到商品编辑页面。
     * @return 跳转到商品编辑页面
     */
    @GetMapping("/goods/add")
    public String editPage(HttpServletRequest request,
                           @RequestParam("parentId") long parentId){

        request.setAttribute("path", "goods-edit");

        //从数据库获取第一层分类列表。
        List<Category> categoryList = categoryService.queryCategoryListByParentId(parentId);
        request.setAttribute("levelOneCategoryList", categoryList);

        return "/admin/newbee_mall_goods_edit";
    }
}
