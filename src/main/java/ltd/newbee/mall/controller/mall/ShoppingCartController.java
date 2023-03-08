package ltd.newbee.mall.controller.mall;

import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ResponseMsgEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.entity.ShoppingCartItem;
import ltd.newbee.mall.service.ShoppingCartItemService;
import ltd.newbee.mall.util.ResponseGenerator;
import ltd.newbee.mall.vo.MallShoppingCartItemVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private ShoppingCartItemService shoppingCartItemService;

    /**
     * 跳转到结算页面
     * 1.获取购物车商品信息
     *      商品信息为空，则不跳转
     * 2.计算总价
     *      总价 < 1,则跳转错误页面
     * @param request
     * @param session
     * @return 跳转到结算页面
     */
    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request, HttpSession session){
        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        List<MallShoppingCartItemVO> shoppingCartItemVOS = shoppingCartItemService.getMyShoppingCartItems(userId);

        if(CollectionUtils.isEmpty(shoppingCartItemVOS)){
            //无数据则不跳转至结算页
            return "/shop-cart";
        }

        int priceTotal = 0;
        //总价
        for(MallShoppingCartItemVO shoppingCartItemVO: shoppingCartItemVOS){
            priceTotal += shoppingCartItemVO.getSellingPrice() * shoppingCartItemVO.getGoodsCount();
        }
        if(priceTotal < 1){
            return "500_mine";
        }

        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", shoppingCartItemVOS);
        return "mall/order-settle";
    }

    /**
     * 删除一条购物车上的商品
     * @param cartItemId 购物车商品的cartItemId
     * @return 响应对象，包含处理结果
     */
    @DeleteMapping("/shop-cart/{cartItemId}")
    @ResponseBody
    public Object deleteAShoppingCartItemByCartItemId(@PathVariable("cartItemId") Long cartItemId){
        if(ObjectUtils.isEmpty(cartItemId)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        String result = shoppingCartItemService.deleteAShoppingCartItemByCartItemId(cartItemId);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResponseGenerator.genSuccessResponse();
        }

        return ResponseGenerator.genFailResponse(result);
    }

    /**
     * 修改购物车中某个商品的数量
     *
     * @param shoppingCartItem 前端提交的购物车商品数据
     * @param session
     * @return 响应对象，包含处理结果
     */
    @PutMapping("/shop-cart")
    @ResponseBody
    public Object updateAGoodsCount(@RequestBody ShoppingCartItem shoppingCartItem,
                                    HttpSession session){

        if(ObjectUtils.isEmpty(shoppingCartItem)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        shoppingCartItem.setUserId(userId);

        String result = shoppingCartItemService.updateAGoodsCount(shoppingCartItem);
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResponseGenerator.genSuccessResponse();
        }

        return ResponseGenerator.genFailResponse(result);
    }

    /**
     * 1.计算购物项总数
     * 2.计算总价
     * 3.访问业务层，获取CartItemVOS
     * 4.保存以上数据到请求域
     * 5.跳转到购物车列表页面
     * @param request
     * @param session
     * @return 购物车列表页面
     */
    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request, HttpSession session){
        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);

        int itemsTotal = 0;
        int priceTotal = 0;

        List<MallShoppingCartItemVO> shoppingCartItemVOS = shoppingCartItemService.getMyShoppingCartItems(userId);

        if(!CollectionUtils.isEmpty(shoppingCartItemVOS)){
            //购物项总数:所有商品的总数量
            itemsTotal = shoppingCartItemVOS.stream().mapToInt(MallShoppingCartItemVO::getGoodsCount).sum();
            if(itemsTotal < 1){
                return "500_mine";
            }
            //总价 p = p + sp * gc;
            for(MallShoppingCartItemVO shoppingCartItemVO: shoppingCartItemVOS){
                priceTotal += shoppingCartItemVO.getSellingPrice() * shoppingCartItemVO.getGoodsCount();
            }
            if(priceTotal < 1){
                return "500_mine";
            }
        }

        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", shoppingCartItemVOS);

        return "mall/cart";
    }

    /**
     * 添加商品到购物车时，生成购物车物品数据
     *
     * @param shoppingCartItem 添加到购物车的商品
     * @param session
     * @return 处理结果
     */
    @PostMapping("/shop-cart")
    @ResponseBody
    public Object saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem,
                                       HttpSession session){

        if(ObjectUtils.isEmpty(shoppingCartItem)){
            return ResponseGenerator.genFailResponse("参数异常");
        }

        Long userId = (Long) session.getAttribute(Constants.MALL_USER_LOGIN_ID);
        shoppingCartItem.setUserId(userId);

        try{
            String result = shoppingCartItemService.saveMallCartItem(shoppingCartItem);
            if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
                //成功
                return ResponseGenerator.genSuccessResponse();
            }else{
                return ResponseGenerator.genFailResponse(result);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.CART_ITEM_SAVE_EXCEPTION.getMessage());
        }
    }
}
