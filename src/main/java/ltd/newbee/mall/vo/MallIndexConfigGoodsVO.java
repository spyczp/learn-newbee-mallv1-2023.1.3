package ltd.newbee.mall.vo;

import lombok.Data;

@Data
public class MallIndexConfigGoodsVO {

    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private String tag;
}
