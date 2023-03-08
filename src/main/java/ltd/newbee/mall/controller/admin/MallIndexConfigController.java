package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.IndexConfigTypeEnum;
import ltd.newbee.mall.entity.MallIndexConfig;
import ltd.newbee.mall.service.MallIndexConfigService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/admin")
public class MallIndexConfigController {

    @Resource
    private MallIndexConfigService mallIndexConfigService;

    /**
     * 删除对应configId的 商城配置信息
     * 1.验证参数合法性
     * 2.访问业务层，提交需要删除的数据
     * 3.给前端返回响应结果
     * @param ids configId组成的数组
     * @return 响应对象，包含删除处理结果
     */
    @PostMapping("/indexConfigs/delete")
    @ResponseBody
    public Object deleteMallIndexConfigs(@RequestBody Long[] ids){

        if(ArrayUtils.isEmpty(ids)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int result = mallIndexConfigService.deleteMallIndexConfigsByIds(ids);
            if(result > 0){
                //成功
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("删除商城配置信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("删除商城配置信息失败");
        }
    }

    /**
     * 修改一条 商城首页配置信息
     * 1.验证参数合法性
     * 2.访问业务层，提交需要修改的数据
     * 3.给前端返回响应结果
     * @param mallIndexConfig 商城首页配置信息
     * @param session
     * @return 响应对象：包含修改处理结果
     */
    @PostMapping("/indexConfigs/update")
    @ResponseBody
    public Object editAMallIndexConfig(@RequestBody MallIndexConfig mallIndexConfig,
                                       HttpSession session){

        if(ObjectUtils.isEmpty(mallIndexConfig.getConfigId()) ||
           !StringUtils.hasText(mallIndexConfig.getConfigName()) ||
           ObjectUtils.isEmpty(mallIndexConfig.getConfigType()) ||
           !StringUtils.hasText(mallIndexConfig.getRedirectUrl()) ||
           ObjectUtils.isEmpty(mallIndexConfig.getGoodsId()) ||
           ObjectUtils.isEmpty(mallIndexConfig.getConfigRank())
        ){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        //封装其它的数据：update_time、update_user
        mallIndexConfig.setUpdateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        mallIndexConfig.setUpdateTime(new Date());

        try{
            int result = mallIndexConfigService.editAMallIndexConfig(mallIndexConfig);
            if(result == 1){
                //成功
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("修改商城配置信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("修改商城配置信息失败");
        }
    }

    /**
     * 新增 商城首页配置信息
     * 1.验证参数合法性
     * 2.访问业务层，提交需要添加的数据
     * 3.给前端返回响应结果
     * @param mallIndexConfig 封装商城首页配置数据
     * @return 响应对象：包含新增处理结果
     */
    @PostMapping("/indexConfigs/save")
    @ResponseBody
    public Object addAMallIndexConfig(@RequestBody MallIndexConfig mallIndexConfig,
                                  HttpSession session){

        if(!StringUtils.hasText(mallIndexConfig.getConfigName()) ||
                ObjectUtils.isEmpty(mallIndexConfig.getConfigType()) ||
                !StringUtils.hasText(mallIndexConfig.getRedirectUrl()) ||
                ObjectUtils.isEmpty(mallIndexConfig.getGoodsId()) ||
                ObjectUtils.isEmpty(mallIndexConfig.getConfigRank())
          ){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        //封装其它未提供的参数：isDeleted、createTime、createUser
        mallIndexConfig.setIsDeleted((byte) 0);
        mallIndexConfig.setCreateUser((Integer) session.getAttribute(Constants.SESSION_LOGIN_USER_ID));
        mallIndexConfig.setCreateTime(new Date());

        try{
            //访问业务层，提交数据
            int result = mallIndexConfigService.addAMallIndexConfig(mallIndexConfig);
            if(result == 1){
                //成功
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse("新增商城首页配置数据失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse("新增商城首页配置数据失败");
        }
    }

    /**
     * 展示首页配置数据列表
     * 1.验证前端提交的参数的合法性
     * 2.若合法，访问业务层，获取分页数据
     * 3.封装分页数据，返回响应对象给前端
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @param configType 配置类型
     * @return 响应数据，包含分页数据
     */
    @GetMapping("/indexConfigs/list")
    @ResponseBody
    public Object list(@RequestParam("pageNum") Integer pageNum,
                       @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("configType") Byte configType){

        if(ObjectUtils.isEmpty(pageNum) ||
        ObjectUtils.isEmpty(pageSize) ||
        ObjectUtils.isEmpty(configType) ||
        pageNum <= 0 ||
        pageSize <0){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        //访问业务层，获取分页数据
        PageResult pageResult = mallIndexConfigService.queryListAndCountByConditionForPagination(pageNum, pageSize, configType);

        return ResponseGenerator.genSuccessResponse(pageResult);
    }

    /**
     * 跳转到首页配置 管理页面
     * 管理首页配置，有三个管理页面，分别是热销商品配置、新品上线配置、为你推荐配置
     * 所以，configType作为判断 配置类型 来显示对应的配置页面
     * 之后在配置页面的模态窗口中也会用到configType
     * @param request
     * @param configType 配置类型
     * @return 跳转到首页配置 管理页面
     */
    @GetMapping("/indexConfigs")
    public String toIndexConfigPage(HttpServletRequest request, Integer configType){

        //根据前端提交的configType配置类型参数查询对应的配置类型
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);

        if(indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)){
            //到这里，说明configType参数异常
            return "500_mine";
        }

        request.setAttribute("path", indexConfigTypeEnum.getName());
        //渲染配置项列表的时候会
        request.setAttribute("configType", configType);

        return "admin/newbee_mall_index_config";
    }
}
