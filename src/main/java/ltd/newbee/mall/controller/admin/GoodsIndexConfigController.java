package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.IndexConfigTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class GoodsIndexConfigController {

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
    public String indexConfigPage(HttpServletRequest request, Integer configType){

        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);

        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);

        return "admin/newbee_mall_index_config";
    }
}
