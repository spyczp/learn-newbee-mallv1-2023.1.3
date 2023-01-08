package ltd.newbee.mall.controller;

import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class PageTestController {

    @Resource
    private UserService userService;

    /**
     * 分页测试
     * 1.获取参数：当前页码、每页显示条数
     * 2.计算起始记录
     * 3.到数据库查询列表数据、总记录数
     * 4.封装数据PageResult，返回给前端
     * @param currentPage 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    @GetMapping("/pageTest")
    public ResponseObj pageTest(@RequestParam("currentPage") Integer currentPage,
                                @RequestParam("pageSize") Integer pageSize){

        //2.计算起始记录
        Integer startNum = (currentPage - 1) * pageSize;

        //3.到数据库查询列表数据、总记录数

        return null;
    }
}
