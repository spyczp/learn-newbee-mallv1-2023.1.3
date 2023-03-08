package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderMapper {

    int insertAnOrder(Order order);

    Order selectByOrderNo(String orderNo);

    List<Order> selectOrderList(Map<String, Object> map);

    int selectCountForList(Map<String, Object> map);

    int selectTotalCountByUserId(Long userId);

    int updateOrderData(Order order);

    int updateOrderStatusByIds(Long[] ids, Byte orderStatus, Date date);

    int updateOrderStatusByOrderNo(String orderNo, Byte orderStatus, Date date);
}
