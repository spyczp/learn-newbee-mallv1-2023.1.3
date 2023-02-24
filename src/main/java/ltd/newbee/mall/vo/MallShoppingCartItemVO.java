package ltd.newbee.mall.vo;

import lombok.Data;

@Data
public class MallShoppingCartItemVO {

    private Long cartItemId;

    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
