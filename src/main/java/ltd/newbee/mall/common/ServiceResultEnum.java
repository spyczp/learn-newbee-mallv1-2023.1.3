package ltd.newbee.mall.common;

public enum ServiceResultEnum {

    GOODS_NOT_EXIST("商品不存在"),
    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("超过单个商品的最大数量限制"),
    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("商品种类超出最大数量"),
    SHOPPING_ITEM_ERROR("购物项错误"),
    SHOPPING_ITEM_COUNT_ERROR("购物项数量错误"),
    ORDER_PRICE_ERROR("订单价格错误"),
    NULL_ADDRESS_ERROR("没有收货地址"),
    ORDER_NOT_EXIST_ERROR("订单不存在"),
    ORDER_STATUS_ERROR("订单状态错误"),
    ORDER_ITEM_NOT_EXIST_ERROR("订单项不存在"),
    NO_PERMISSION_ERROR("非关联用户的订单"),
    SUCCESS("success"),
    DB_ERROR("数据库错误"),
    DATA_NOT_EXIST("数据不存在"),
    DB_ACCESS_EXCEPTION("数据库访问异常")
    ;

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    ServiceResultEnum(String result) {
        this.result = result;
    }
}
