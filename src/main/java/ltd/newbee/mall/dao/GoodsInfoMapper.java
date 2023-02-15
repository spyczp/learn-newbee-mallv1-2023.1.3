package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.GoodsInfo;

import java.util.List;

public interface GoodsInfoMapper {

    int insertAGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo selectGoodsInfoByGoodsId(Long goodsId);

    List<GoodsInfo> selectListByGoodsIds(List<Long> goodsIds);

    int updateAGoodsInfo(GoodsInfo goodsInfo);

    List<GoodsInfo> selectGoodsInfoListForPagination(Integer startIndex, Integer pageSize);

    int selectCountForPagination();

    int updateGoodsSellStatusByIdsAndStateNum(Long[] ids, Integer status);
}
