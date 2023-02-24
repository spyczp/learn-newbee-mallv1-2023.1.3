package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.dao.ShoppingCartItemMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.entity.ShoppingCartItem;
import ltd.newbee.mall.service.ShoppingCartItemService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShoppingCartItemServiceImpl implements ShoppingCartItemService {

    @Resource
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 修改购物车中一个商品的数量
     * 1.查询数据库，看对应cartItemId的数据是否存在
     * 2.如果要修改的商品数量大于限定值，则提示错误
     * 3.向数据库提交更新的数据，更新了 商品数量、修改时间
     * 4.返回处理结果
     * @param shoppingCartItem 前端提交的购物车商品数据
     * @return 处理结果
     */
    @Override
    public String updateAGoodsCount(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItem item = shoppingCartItemMapper.selectByCartItemId(shoppingCartItem.getCartItemId());
        if(ObjectUtils.isEmpty(item)){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        if(shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }

        item.setGoodsCount(shoppingCartItem.getGoodsCount());
        item.setUpdateTime(new Date());

        try{
            int result = shoppingCartItemMapper.updateAGoodsCount(item);
            if(result > 0){
                //处理成功
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServiceResultEnum.DB_ACCESS_EXCEPTION.getResult();
        }
    }

    /**
     * 删除一条购物车中的商品
     * 实际是在数据库把该条数据的isDeleted改为1
     * @param cartItemId 该条数据的id
     * @return 处理结果
     */
    @Override
    public String deleteAShoppingCartItemByCartItemId(Long cartItemId) {
        ShoppingCartItem shoppingCartItem = shoppingCartItemMapper.selectByCartItemId(cartItemId);
        if(ObjectUtils.isEmpty(shoppingCartItem)){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        try{
            int result = shoppingCartItemMapper.updateIsDeletedToOne(cartItemId);
            if(result > 0){
                //修改成功
                return ServiceResultEnum.SUCCESS.getResult();
            }else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServiceResultEnum.DB_ACCESS_EXCEPTION.getResult();
        }
    }

    /**
     * 添加商品到购物车
     * 1.判断购物车中是否已经存在商品
     * 2.判断该商品是否为空，根据购物车类的goodsId
     * 3.判断是否超出单个商品的最大数量
     * 4.判断是否超出最大数量
     * 5.均正常，则保存记录
     * @param shoppingCartItem 本条购物车数据
     * @return 处理结果字符串
     */
    @Override
    public String saveMallCartItem(ShoppingCartItem shoppingCartItem) throws Exception {
        ShoppingCartItem item = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if(item != null){
            return "该商品已在购物车中";
        }

        GoodsInfo goodsInfo = goodsInfoMapper.selectGoodsInfoByGoodsId(shoppingCartItem.getGoodsId());
        if(ObjectUtils.isEmpty(goodsInfo)){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }

        if(shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }

        int totalItem = shoppingCartItemMapper.selectCountByUserId(shoppingCartItem.getUserId());
        if(totalItem >= Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }

        int result = shoppingCartItemMapper.insertAShoppingCartItem(shoppingCartItem);
        if(result > 0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {

        ArrayList<MallShoppingCartItemVO> mallShoppingCartItemVOS  = new ArrayList<>();
        List<ShoppingCartItem> shoppingCartItems = shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);

        if(!CollectionUtils.isEmpty(shoppingCartItems)){
            //获取购物车下所有商品的id
            List<Long> goodsIds = shoppingCartItems.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectListByGoodsIds(goodsIds);
            Map<Long, GoodsInfo> goodsInfoMap = new HashMap<>();

            // toMap Returns a Collector that accumulates elements into a Map whose keys and values are the result of applying the provided mapping functions to the input elements.
            //(entity1, entity2) -> entity1) 猜测：键重复时的处理mergeFunction – a merge function, used to resolve collisions between values associated with the same key
            //Function.identity() Returns a function that always returns its input argument.
            if(!CollectionUtils.isEmpty(goodsInfoList)){
                 goodsInfoMap = goodsInfoList.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }

            /**
             * 1.遍历shoppingCartItems,把同名的属性的值拷贝到MallShoppingCartItemVO对象
             * 2.把对应的goodsInfo的同名的属性的值拷贝到MallShoppingCartItemVO对象
             * 3.把goodsName长度过长的截短
             */
            for(ShoppingCartItem shoppingCartItem: shoppingCartItems){
                MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                BeanUtil.copyProperties(shoppingCartItem, mallShoppingCartItemVO);
                if(goodsInfoMap.containsKey(shoppingCartItem.getGoodsId())){
                    //把对应的goodsInfo的同名的属性的值拷贝到MallShoppingCartItemVO对象
                    GoodsInfo goodsInfo = goodsInfoMap.get(shoppingCartItem.getGoodsId());
                    mallShoppingCartItemVO.setGoodsCoverImg(goodsInfo.getGoodsCoverImg());
                    mallShoppingCartItemVO.setSellingPrice(goodsInfo.getSellingPrice());
                    String goodsName = goodsInfo.getGoodsName();
                    if(goodsName.length() > 28){
                        goodsName  = goodsName.substring(0, 28) + "...";
                    }
                    mallShoppingCartItemVO.setGoodsName(goodsName);
                    mallShoppingCartItemVOS.add(mallShoppingCartItemVO);
                }
            }
        }

        return mallShoppingCartItemVOS;
    }
}
