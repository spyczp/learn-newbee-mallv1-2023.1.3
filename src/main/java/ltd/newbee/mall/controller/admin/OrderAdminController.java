package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.common.MallOrderStatusEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.ArrayUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/admin/orders")
public class OrderAdminController {

    @Resource
    private OrderService orderService;

    /**
     * 修改订单信息
     * 1.判断order是否为空
     * 2.访问业务层，修改订单信息
     * 3.返回处理结果
     * @param order 订单信息
     * @return 处理结果
     */
    @PostMapping("/update")
    @ResponseBody
    public Object updateOrder(@RequestBody Order order){
        if(ObjectUtils.isEmpty(order)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderData(order);
            if(count > 0){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 关闭订单
     * 1.判断ids是否为空
     * 2.访问业务层，修改订单状态为“商家关闭”
     * 3.返回处理结果
     * @param ids 订单数组
     * @return 处理结果
     */
    @PostMapping("/close")
    @ResponseBody
    public Object closeOrder(@RequestBody Long[] ids){
        if(ArrayUtils.isEmpty(ids)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderStatusByIds(ids, (byte) MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus(), new Date());
            if(count > 0){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 修改订单状态为“出库成功”
     * 1.判断ids是否为空
     * 2.访问业务层，修改订单状态
     * 3.返回处理结果
     * @param ids 订单数组
     * @return 处理结果数据
     */
    @PostMapping("/checkOut")
    @ResponseBody
    public Object checkOut(@RequestBody Long[] ids){
        if(ArrayUtils.isEmpty(ids)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderStatusByIds(ids, (byte) MallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus(), new Date());
            if(count > 0){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 修改订单状态为“配货完成”
     * @param ids 订单数组
     * @return 处理结果数据
     */
    @PostMapping("/checkDone")
    @ResponseBody
    public Object checkDone(@RequestBody Long[] ids){

        if(ArrayUtils.isEmpty(ids)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderStatusByIds(ids, (byte) MallOrderStatusEnum.ORDER_PACKAGED.getOrderStatus(), new Date());
            if(count > 0){
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 展示订单列表
     * 1.判断前端提交的参数是否正常
     * 2.访问业务层，获取分页数据
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 响应数据，包含订单列表
     */
    @GetMapping("/list")
    @ResponseBody
    public Object orderList(@RequestParam("pageNum") Integer pageNum,
                            @RequestParam("pageSize") Integer pageSize){

        if(ObjectUtils.isEmpty(pageNum) ||
           ObjectUtils.isEmpty(pageSize)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        PageResult pageResult = orderService.getOrderListForAdmin(pageNum, pageSize);
        return ResponseGenerator.genSuccessResponse(pageResult);
    }


    /**
     * 订单管理页面
     * @param request
     * @return 订单管理页面
     */
    @GetMapping("")
    public String orderPage(HttpServletRequest request){
        request.setAttribute("path", "orders");
        return "admin/newbee_mall_order";
    }
}
