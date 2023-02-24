package ltd.newbee.mall.common;

public enum ServiceResultEnum {

    GOODS_NOT_EXIST("商品不存在"),
    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("超过单个商品的最大数量限制"),
    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("商品种类超出最大数量"),
    SUCCESS("success"),
    DB_ERROR("数据库错误"),
    DATA_NOT_EXIST("数据不存在"),
    DB_ACCESS_EXCEPTION("数据库访问异常");
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
