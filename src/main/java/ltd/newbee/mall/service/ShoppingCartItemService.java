package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.ShoppingCartItem;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;

import java.util.List;

public interface ShoppingCartItemService {

    String updateAGoodsCount(ShoppingCartItem shoppingCartItem);

    String deleteAShoppingCartItemByCartItemId(Long cartItemId);

    String saveMallCartItem(ShoppingCartItem shoppingCartItem) throws Exception;

    List<MallShoppingCartItemVO> getMyShoppingCartItems(Long userId);
}
