package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.MallOrderStatusEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.exception.MallException;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.service.ShoppingCartItemService;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.ResponseGenerator;
import ltd.newbee.mall.vo.MallOrderDetailVO;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private ShoppingCartItemService shoppingCartItemService;

    @Resource
    private OrderService orderService;

    /**
     * 用户端取消订单
     * 1.判断参数是否为空
     * 2.访问业务层，修改订单状态为“手动关闭”
     * 3.返回处理结果
     * @param orderNo 订单号
     * @return 处理结果
     */
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Object cancelOrder(@PathVariable("orderNo") String orderNo){
        if(!StringUtils.hasText(orderNo)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderStatusByOrderNo(orderNo, (byte) MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus(), new Date());
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
     * 用户确认收货，修改订单状态为“交易成功”
     * 1.判断orderNo是否为空
     * 2.访问业务层，修改订单状态
     * 3.返回处理结果
     * @param orderNo 订单号
     * @return 处理结果
     */
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Object finishOrder(@PathVariable("orderNo") String orderNo){
        if(!StringUtils.hasText(orderNo)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        try{
            int count = orderService.updateOrderStatusByOrderNo(orderNo, (byte) MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus(), new Date());
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
     * 选择支付类型后，跳转到对应类型的支付页面
     * @param orderNo 订单号
     * @param payType 支付类型
     * @param request
     * @return 跳转到对应类型的支付页面
     */
    @GetMapping("/payPage")
    public String payPage(@RequestParam("orderNo") String orderNo,
                          @RequestParam("payType") Byte payType,
                          HttpServletRequest request,
                          HttpSession session){

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        Order order = orderService.getOrderByOrderNo(orderNo);
        //判断userId
        if(!userId.equals(order.getUserId())){
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if(order.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            MallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());

        if(payType == 1){
            return "mall/alipay";
        }else{
            return "mall/wxpay";
        }
    }

    /**
     * 跳转到订单支付方式选择页面
     * @param orderNo 订单号
     * @param request
     * @param session
     * @return 订单支付方式选择页面
     */
    @GetMapping("/selectPayType")
    public String selectPayType(@RequestParam("orderNo") String orderNo,
                                HttpServletRequest request,
                                HttpSession session){

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        Order order = orderService.getOrderByOrderNo(orderNo);
        //判断userId
        if(!userId.equals(order.getUserId())){
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if(order.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            MallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", order.getTotalPrice());

        return "mall/pay-select";
    }


    @GetMapping("/paySuccess")
    @ResponseBody
    public Object paySuccess(@RequestParam("orderNo") String orderNo,
                             @RequestParam("payType") int payType){

        String result = orderService.paySuccess(orderNo, payType);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResponseGenerator.genSuccessResponse();
        }else{
            return ResponseGenerator.genFailResponse(result);
        }
    }

    /**
     * 跳转到订单列表页
     * 1.封装数据：userId、pageNum、pageSize
     * 2.访问业务层，获得订单列表VOS
     * 3.保存分页数据到请求域
     * 4.跳转到订单列表页
     * @param params 前端提交的参数
     * @param request
     * @param session
     * @return 订单列表页
     */
    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params,
                                HttpServletRequest request,
                                HttpSession session){

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        params.put("userId", userId);
        if(ObjectUtils.isEmpty(params.get("pageNum"))){
            params.put("pageNum", 1);
        }
        params.put("pageSize", Constants.ORDER_SEARCH_PAGE_LIMIT);

        PageResult orderPageResult = orderService.getMyOrders (params);

        request.setAttribute("orderPageResult", orderPageResult);
        request.setAttribute("path", "orders");

        return "mall/my-orders";
    }

    /**
     * 中转处理，是防止订单号显示在地址路径中
     * 1.根据用户id获得订单详情
     * 2.把订单详情保存到请求域
     * 3.跳转到订单详情页面
     * @param request
     * @param session
     * @return
     */
    @GetMapping("/orders/{orderNo}")
    private String orderDetailPage(@PathVariable("orderNo") String orderNo,
                                   HttpServletRequest request,
                                   HttpSession session){
        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        MallOrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, userId);
        if(ObjectUtils.isEmpty(orderDetailVO)){
            return "500_mine";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    /**
     * 跳转到订单详情页
     * 1.检查用户是否有收货地址
     * 2.检查购物车中是否有数据
     * 3.保存订单并返回订单号
     * 4.跳转到订单详情页
     * @param session
     * @return
     */
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession session){
        String userAddress = (String) session.getAttribute(Constants.MALL_USER_ADDRESS);
        if(!StringUtils.hasText(userAddress)){
            //无收货地址
            MallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        List<MallShoppingCartItemVO> myShoppingCartItems = shoppingCartItemService.getMyShoppingCartItems(userId);
        if(CollectionUtils.isEmpty(myShoppingCartItems)){
            //购物车中无数据则跳转至错误页
            MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }

        User user = new User();
        user.setUserId(userId);
        user.setAddress(userAddress);

        //保存订单并返回订单号
        String saveOrderResult  = orderService.saveOrder(user, myShoppingCartItems);

        //跳转到订单详情页
        return "redirect:orders/" + saveOrderResult ;
    }
}
