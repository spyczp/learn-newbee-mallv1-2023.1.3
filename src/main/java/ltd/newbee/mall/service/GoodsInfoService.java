package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

public interface GoodsInfoService {

    int createAGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo queryGoodsInfoByGoodsId(Long goodsId);

    int editAGoodsInfo(GoodsInfo goodsInfo);

    PageResult queryGoodsInfoListForPagination(Integer pageNum, Integer pageSize);

    int changeGoodsSellStatusByIdsAndStateNum(Long[] ids, Integer status);


}
