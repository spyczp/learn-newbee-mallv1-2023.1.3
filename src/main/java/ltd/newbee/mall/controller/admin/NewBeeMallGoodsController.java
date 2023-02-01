package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.util.ImageData;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
public class NewBeeMallGoodsController {

    @Resource
    private CategoryService categoryService;

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
    @GetMapping("/goods/edit")
    public String editPage(HttpServletRequest request,
                           @RequestParam("parentId") long parentId){

        request.setAttribute("path", "goods-edit");

        //从数据库获取第一层分类列表。
        List<Category> categoryList = categoryService.queryCategoryListByParentId(parentId);
        request.setAttribute("levelOneCategoryList", categoryList);

        return "/admin/newbee_mall_goods_edit";
    }
}
