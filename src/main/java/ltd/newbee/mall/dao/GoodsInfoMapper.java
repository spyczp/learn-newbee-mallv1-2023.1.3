package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.GoodsInfo;

import java.util.List;
import java.util.Map;

public interface GoodsInfoMapper {

    int selectCountForMall(Map<String, Object> map);

    List<GoodsInfo> selectGoodsInfoListByConditionForMall(Map<String, Object> map);

    int insertAGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo selectGoodsInfoByGoodsId(Long goodsId);

    List<GoodsInfo> selectListByGoodsIds(List<Long> goodsIds);

    int updateAGoodsInfo(GoodsInfo goodsInfo);

    List<GoodsInfo> selectGoodsInfoListForPagination(Integer startIndex, Integer pageSize);

    int selectCountForPagination();

    int updateGoodsSellStatusByIdsAndStateNum(Long[] ids, Integer status);
}
