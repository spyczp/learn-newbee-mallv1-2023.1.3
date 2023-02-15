package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.MallIndexConfig;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.MallIndexConfigGoodsVO;

import java.util.List;

public interface MallIndexConfigService {

    PageResult queryListAndCountByConditionForPagination(Integer pageNum, Integer pageSize, Byte contentType);

    int addAMallIndexConfig(MallIndexConfig mallIndexConfig);

    int editAMallIndexConfig(MallIndexConfig mallIndexConfig);

    int deleteMallIndexConfigsByIds(Long[] ids);

    List<MallIndexConfigGoodsVO> queryGoodsForIndexShow(Byte configType, Integer count);
}
