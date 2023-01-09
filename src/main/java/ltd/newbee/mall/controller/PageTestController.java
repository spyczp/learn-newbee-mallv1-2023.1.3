package ltd.newbee.mall.controller;

import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class PageTestController {

    @Resource
    private UserService userService;

    /**
     * 分页测试
     * 1.获取参数：当前页码、每页显示条数
     * 2.计算起始记录
     * 3.到数据库查询列表数据、总记录数
     * 4.封装数据到PageResult
     * 5.创建响应对象，封装数据，返回给前端
     * @param currentPage 当前页
     * @param pageSize 每页显示条数
     * @return 响应对象 返回给前端
     */
    @GetMapping("/pageTest")
    public Object pageTest(@RequestParam("currentPage") Integer currentPage,
                                @RequestParam("pageSize") Integer pageSize){

        //2.计算起始记录
        Integer startNum = (currentPage - 1) * pageSize;

        //3.到数据库查询列表数据、总记录数
        //4.封装数据到PageResult
        PageResult pageResult = userService.queryUsersForPagination(startNum, pageSize);
        pageResult.setCurrentPage(currentPage);

        //5.创建响应对象，封装数据，返回给前端
        ResponseObj<PageResult> responseObj = new ResponseObj<>();
        responseObj.setCode(0);
        responseObj.setMessage("success");
        responseObj.setData(pageResult);

        return responseObj;
    }
}
