package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.*;
import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.dao.OrderItemMapper;
import ltd.newbee.mall.dao.OrderMapper;
import ltd.newbee.mall.dao.ShoppingCartItemMapper;
import ltd.newbee.mall.entity.*;
import ltd.newbee.mall.exception.MallException;
import ltd.newbee.mall.service.OrderService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.NumberUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.MallOrderDetailVO;
import ltd.newbee.mall.vo.MallOrderItemVO;
import ltd.newbee.mall.vo.MallOrderListVO;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private GoodsInfoMapper goodsInfoMapper;

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    /**
     * 在管理页面展示订单分页数据
     * 1.计算startIndex
     * 2.访问数据库，获取订单列表和订单数
     * 3.封装分页数据
     * 4.返回分页数据
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 订单分页数据
     */
    public PageResult getOrderListForAdmin(Integer pageNum, Integer pageSize){
        Integer startIndex= (pageNum - 1) * pageSize;

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageSize", pageSize);
        map.put("startIndex", startIndex);

        List<Order> orderList = orderMapper.selectOrderList(map);
        int totalCount = orderMapper.selectCountForList(map);

        return new PageResult(pageNum, pageSize, totalCount, orderList);
    }

    @Override
    public int updateOrderStatusByIds(Long[] ids, Byte orderStatus, Date date) {
        return orderMapper.updateOrderStatusByIds(ids, orderStatus, date);
    }

    @Override
    public int updateOrderStatusByOrderNo(String orderNo, Byte orderStatus, Date date) {
        return orderMapper.updateOrderStatusByOrderNo(orderNo, orderStatus, date);
    }

    @Override
    public int updateOrderData(Order order) {
        return orderMapper.updateOrderData(order);
    }

    /**
     * 生成订单
     * 1.检查购物项是否存在
     * 2.检查商品是否下架
     *      获得所有购物项id
     *      获得所有商品id
     *      根据商品id查询商品信息
     *      检查是否包含已下架商品
     * 3.检查是否还有库存
     *      获得GoodsMap
     *      判断商品库存
     * 4.生成订单
     *      删除购物项
     *      生成订单号
     *      保存订单
     * 5.返回处理结果
     * @param user 登录用户的信息
     * @param myShoppingCartItems 购物车数据
     * @return 处理结果
     */
    @Override
    @Transactional
    public String saveOrder(User user, List<MallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIdList = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectListByGoodsIds(goodsIdList);
        //检查是否包含已下架商品
        List<GoodsInfo> goodsListNotSelling = goodsInfoList.stream()
                                                           .filter(goodsTemp -> goodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                                                           .collect(Collectors.toList());

        //如果有已下架的商品，则抛出异常，结束本方法
        if(!CollectionUtils.isEmpty(goodsListNotSelling)){
            //goodsListNotSelling 对象非空则表示有下架商品
            String message = "";
            for(GoodsInfo goodsInfo: goodsListNotSelling){
                message += goodsInfo.getGoodsId() + ", ";
            }
            //去掉最后的逗号
            message = message.substring(0, -2);
            message += "已下架，无法生成订单";
            //抛出异常
            MallException.fail(message);
        }

        //执行到这里，说明没有已下架的商品
        //判断商品库存
        Map<Long, GoodsInfo> goodsMap = goodsInfoList.stream()
                                                     .collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));

        //购物项中的goodsId存在不正确的情况（因为是前端提交的），所以查询到的商品信息可能不是全部都有。
        //所以先判断一下购物项对应的商品是否存在
        for(MallShoppingCartItemVO mallShoppingCartItemVO: myShoppingCartItems){
            if(!goodsMap.containsKey(mallShoppingCartItemVO.getGoodsId())){
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if(mallShoppingCartItemVO.getGoodsCount() > goodsMap.get(mallShoppingCartItemVO.getGoodsId()).getStockNum()){
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }

        //执行到这里，说明商品的库存也正常
        if(!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIdList) && !CollectionUtils.isEmpty(goodsInfoList)){
            if(shoppingCartItemMapper.updateIsDeletedToOneByList(itemIdList) > 0){
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                //修改商品的库存数量
                int updateStockNumResult = goodsInfoMapper.updateStockNum(stockNumDTOS);
                if(updateStockNumResult < 1){
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                //保存订单
                Order order = new Order();
                order.setOrderNo(orderNo);
                order.setUserId(user.getUserId());
                order.setUserAddress(user.getAddress());

                //总价
                int priceTotal = 0;
                //计算总价
                for(MallShoppingCartItemVO shoppingCartItemVO: myShoppingCartItems){
                    priceTotal += shoppingCartItemVO.getSellingPrice() * shoppingCartItemVO.getGoodsCount();
                }
                //总价不合理, 则报异常
                if(priceTotal < 1){
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(priceTotal);
                //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                order.setExtraInfo(extraInfo);

                //生成订单项并保存订单项纪录
                if(orderMapper.insertAnOrder(order) > 0){
                    //生成所有的订单项快照，并保存至数据库
                    List<OrderItem> orderItemList = new ArrayList<>();
                    for(MallShoppingCartItemVO shoppingCartItemVO: myShoppingCartItems){
                        OrderItem orderItem = new OrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(shoppingCartItemVO, orderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        orderItem.setOrderId(order.getOrderId());
                        orderItemList.add(orderItem);
                    }
                    //保存至数据库
                    if(orderItemMapper.insertBatch(orderItemList) > 0){
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    /**
     * 1.根据订单号拿到订单、判断
     * 2.验证是否是当前userId下的订单，否则报错
     * 3.根据orderId查询订单项、判断
     * 4.订单项数据转为订单项VO
     * 5.订单转为订单详情VO
     *      设置订单状态String
     *      设置支付类型String
     *      设置订单项VO
     * 6.返回订单详情VO
     * @param orderNo 订单号
     * @param userId 用户id
     * @return 订单详情VO
     */
    @Override
    public MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        //根据订单号拿到订单、判断
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(ObjectUtils.isEmpty(order)){
            MallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }

        //验证是否是当前userId下的订单，否则报错
        if(!userId.equals(order.getUserId())){
            MallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }

        //根据orderId查询订单项、判断
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(order.getOrderId());
        if(CollectionUtils.isEmpty(orderItemList)){
            MallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        //订单项数据转为订单项VO
        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(orderItemList, MallOrderItemVO.class);

        //订单转为订单详情VO
        //设置订单状态String
        //设置支付类型String
        //设置订单项VO
        MallOrderDetailVO mallOrderDetailVO = new MallOrderDetailVO();
        BeanUtil.copyProperties(order, mallOrderDetailVO);
        mallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderDetailVO.getOrderStatus()).getName());
        mallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(mallOrderDetailVO.getPayType()).getName());
        mallOrderDetailVO.setMallOrderItemVOS(mallOrderItemVOS);

        return mallOrderDetailVO;
    }

    /**
     * 我的订单列表
     * 1.获得当前用户的订单总数
     * 2.获得当前用户的订单列表
     * 3.如果有订单，则把订单转为订单VO
     *      设置订单VO的订单状态String
     * 4.获得该用户所有的订单id，根据订单id查询订单项
     * 5.创建一个订单项map<订单id, 订单项列表>
     * 6.遍历订单VOS
     *      订单项map中有这个订单id的话，拿到对应的订单项列表
     *      将订单项列表转为订单项VO列表
     *      把这个订单项VO列表封装到订单VO中
     * 7.封装分页数据
     * 8.返回分页数据：orderListVOS, total, limit, page
     * @param map 条件参数
     * @return 分页数据
     */
    @Override
    public PageResult getMyOrders(Map<String, Object> map) {
        //获得当前用户的订单总数
        Long userId = (Long) map.get("userId");
        int totalCount = orderMapper.selectTotalCountByUserId(userId);

        Integer pageNum = (Integer) map.get("pageNum");
        Integer pageSize = (Integer) map.get("pageSize");
        Integer startIndex = (pageNum - 1) * pageSize;
        map.put("startIndex", startIndex);
        //获得当前用户的订单列表
        List<Order> orderList = orderMapper.selectOrderList(map);
        List<MallOrderListVO> orderListVOS = new ArrayList<>();
        if(totalCount > 0){
            //如果有订单，则把订单转为订单VO
            orderListVOS = BeanUtil.copyList(orderList, MallOrderListVO.class);
            //设置订单VO的订单状态String
            for(MallOrderListVO mallOrderListVO: orderListVOS){
                String orderStatusString = MallOrderStatusEnum.getMallOrderStatusEnumByStatus(mallOrderListVO.getOrderStatus()).getName();
                mallOrderListVO.setOrderStatusString(orderStatusString);
            }
            //获得该用户所有的订单id，根据订单id查询订单项
            List<Long> orderIds = orderList.stream().map(Order::getOrderId).collect(Collectors.toList());
            //从数据库拿到的订单，order_id是主键，理论上不可能为空,这里为啥还要判断一遍
            if(!CollectionUtils.isEmpty(orderIds)){
                //根据订单id查询订单项
                List<OrderItem> orderItemList = orderItemMapper.selectByOrderIds(orderIds);
                //创建一个订单项map<订单id, 订单项列表>
                Map<Long, List<OrderItem>> itemByOrderIdMap = orderItemList.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));
                //遍历订单VOS
                for(MallOrderListVO mallOrderListVO: orderListVOS){
                    if(itemByOrderIdMap.containsKey(mallOrderListVO.getOrderId())){
                        //订单项map中有这个订单id的话，拿到对应的订单项列表
                        List<OrderItem> orderItemsTemp = itemByOrderIdMap.get(mallOrderListVO.getOrderId());
                        //将订单项列表转为订单项VO列表
                        List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(orderItemsTemp, MallOrderItemVO.class);
                        //把这个订单项VO列表封装到订单VO中
                        mallOrderListVO.setMallOrderItemVOS(mallOrderItemVOS);
                    }
                }
            }
        }

        PageResult pageResult = new PageResult(pageNum, pageSize, totalCount, orderListVOS);

        return pageResult;
    }

    /**
     * 修改订单支付状态为成功
     * 根据orderId查询订单
     *      订单不存在，报错
     *      订单存在，
     *          订单状态判断 非待支付状态下不进行修改操作
     *          封装修改后的订单数据：setOrderStatus、setPayType、setPayStatus、setPayTime、setUpdateTime
     *          到数据库更新订单数据
     *              处理成功，返回成功信息
     *              处理失败，返回失败信息
     * @param orderNo
     * @param payType
     * @return 修改结果
     */
    @Override
    public String paySuccess(String orderNo, int payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(!ObjectUtils.isEmpty(order)){
            //订单状态判断 非待支付状态下不进行修改操作
            if(order.getOrderStatus().intValue() != MallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单数据
            order.setOrderStatus((byte) MallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            order.setPayType((byte) payType);
            order.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            //到数据库更新订单数据
            int result = orderMapper.updateOrderData(order);
            if(result > 0){
                //成功
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

}
