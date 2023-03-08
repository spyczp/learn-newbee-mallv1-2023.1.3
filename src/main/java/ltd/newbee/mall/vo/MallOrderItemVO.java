package ltd.newbee.mall.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * 订单详情页页面订单项VO
 */
@Data
public class MallOrderItemVO implements Serializable {

    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
