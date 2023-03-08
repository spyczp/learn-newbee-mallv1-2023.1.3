package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.OrderItem;

import java.util.List;

public interface OrderItemMapper {

    int insertBatch(List<OrderItem> orderItemList);

    List<OrderItem> selectByOrderId(Long orderId);

    List<OrderItem> selectByOrderIds(List<Long> orderIds);
}
