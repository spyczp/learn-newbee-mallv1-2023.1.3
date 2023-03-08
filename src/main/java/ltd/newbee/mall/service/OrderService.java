package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.Order;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.MallOrderDetailVO;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderService {

    String saveOrder(User user, List<MallShoppingCartItemVO> myShoppingCartItems);

    MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    PageResult getMyOrders(Map<String, Object> map);

    String paySuccess(String orderNo, int payType);

    Order getOrderByOrderNo(String orderNo);

    PageResult getOrderListForAdmin(Integer pageNum, Integer pageSize);

    int updateOrderStatusByIds(Long[] ids, Byte orderStatus, Date date);

    int updateOrderStatusByOrderNo(String orderNo, Byte orderStatus, Date date);

    int updateOrderData(Order order);
}
