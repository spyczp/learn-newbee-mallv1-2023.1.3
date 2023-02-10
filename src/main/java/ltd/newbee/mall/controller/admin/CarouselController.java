package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.stereotype.Controller;
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
import java.util.Random;

import static ltd.newbee.mall.common.Constants.FILE_UPLOAD_DIC;

@Controller
@RequestMapping("/admin")
public class CarouselController {

    @Resource
    private CarouselService carouselService;


    /**
     * 根据id查询轮播图，用于轮播图编辑
     * 1.拿到轮播图id
     * 2.到数据库查询轮播图信息
     * 3.返回信息给前端
     * @param id 轮播图id
     * @return 响应对象
     */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Object getCarouselByIdForEdit(@PathVariable("id") Integer id){
        if(id == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }
        Carousel carousel = carouselService.queryCarouselById(id);
        if(carousel == null){
            return ResponseGenerator.genFailResponse("该轮播图不存在");
        }else{
            return ResponseGenerator.genSuccessResponse(carousel);
        }
    }

    /**
     * 删除选中的轮播图，实际是修改isDeleted为1
     * 1.拿到前端提交的数据：id组成的数组
     * 2.把ids提交到数据库，修改制定id的数据的isDeleted属性值为1
     * 3.返回操作结果
     * 4.返回响应数据给前端
     * @param ids
     * @return 响应对象
     */
    @PostMapping("/carousels/delete")
    @ResponseBody
    public Object deleteCarousels(@RequestBody Integer[] ids,
                                  HttpSession session){

        if(ids == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }
        ResponseObj responseObj = null;
        try{
            Integer loginUserId = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID);
            int result = carouselService.deleteCarousel(ids, loginUserId);
            if(result > 0){
                //代表修改成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                responseObj = ResponseGenerator.genFailResponse("删除轮播图失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("删除轮播图失败");
        }
        return responseObj;
    }

    /**
     * 修改轮播图信息
     * 1.拿到前端提交的数据：carouselId、carouselUrl、carouselRank、redirectUrl
     * 2.需要生成的数据：updateTime、updateUser
     * 3.提交数据到数据库
     * @param carousel 前端提交的轮播图数据，封装到Carousel中
     * @return 响应对象
     */
    @PostMapping("/carousels/update")
    @ResponseBody
    public Object editCarousel(@RequestBody Carousel carousel,
                               HttpSession session){

        if(carousel == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        carousel.setUpdateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        carousel.setUpdateTime(new Date());
        ResponseObj responseObj = null;
        try{
            int result = carouselService.editACarousel(carousel);
            if(result == 1){
                //代表修改成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                responseObj = ResponseGenerator.genFailResponse("修改轮播图信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("修改轮播图信息失败");
        }
        return responseObj;
    }

    /**
     * 保存轮播图信息到数据库
     * 1.拿到前端提交的数据
     * 2.需要生成的数据：carouselId(数据库自增)、isDeleted(0代表未删除)、createTime、createUser(从session中获取登录用户的id)
     * 3.提交数据到数据库
     * @param carousel 前端提交的轮播图数据，封装到Carousel中
     * @return 响应对象
     */
    @PostMapping("/carousels/save")
    @ResponseBody
    public Object saveCarousel(@RequestBody Carousel carousel,
                               HttpSession session){

        if(carousel == null){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        Integer loginUserId = (Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID);
        carousel.setCreateUser(loginUserId);
        byte num = 0;
        carousel.setIsDeleted(new Byte(num));
        carousel.setCreateTime(new Date());

        ResponseObj responseObj = null;
        try{
            //提交数据到数据库
            int result = carouselService.saveACarousel(carousel);
            if(result == 1){
                //代表成功
                responseObj = ResponseGenerator.genSuccessResponse();
            }else{
                //代表失败
                responseObj = ResponseGenerator.genFailResponse("保存轮播图失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("保存轮播图失败");
        }
        return responseObj;
    }

    /**
     * 新增轮播图-上传图片
     * 1.判断前端上传的文件是否为空
     * 2.生成新的本地文件名
     * 3.保存文件到本地
     * 4.返回数据到前端
     *  data：本地文件访问路径
     * @param file 轮播图片
     * @return 响应对象
     */
    @PostMapping("/upload/file")
    @ResponseBody
    public Object uploadCarouselImg(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseGenerator.genFailResponse("获取文件异常");
        }
        //获取文件名后缀
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.indexOf("."));
        //生成新的本地文件名
        Random r = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        String newFileName = stringBuilder.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName).toString();
        ResponseObj responseObj = null;
        //保存文件到本地
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FILE_UPLOAD_DIC, newFileName);
            Files.write(path, bytes);
            responseObj = ResponseGenerator.genSuccessResponse("/upload/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            responseObj = ResponseGenerator.genFailResponse("上传图片保存失败");
        }
        return responseObj;
    }

    /**
     * 展示轮播图列表
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    @GetMapping("/carousels/list")
    @ResponseBody
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
