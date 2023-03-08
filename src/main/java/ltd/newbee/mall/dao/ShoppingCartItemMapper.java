package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingCartItemMapper {

    ShoppingCartItem selectByCartItemId(Long cartItemId);

    int updateAGoodsCount(ShoppingCartItem shoppingCartItem);

    int updateIsDeletedToOne(Long cartItemId);

    int updateIsDeletedToOneByList(List<Long> itemIdList);

    ShoppingCartItem selectByUserIdAndGoodsId(Long userId, Long goodsId);

    int selectCountByUserId(Long userId);

    int insertAShoppingCartItem(ShoppingCartItem shoppingCartItem);

    List<ShoppingCartItem> selectByUserId(@Param("userId") Long userId, @Param("number") int number);
}
