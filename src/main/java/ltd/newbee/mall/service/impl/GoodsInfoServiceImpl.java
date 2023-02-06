package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.service.GoodsInfoService;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsInfoServiceImpl implements GoodsInfoService {

    @Resource
    private GoodsInfoMapper goodsInfoMapper;

    @Override
    public int createAGoodsInfo(GoodsInfo goodsInfo) {
        return goodsInfoMapper.insertAGoodsInfo(goodsInfo);
    }

    @Override
    public GoodsInfo queryGoodsInfoByGoodsId(Long goodsId) {
        return goodsInfoMapper.selectGoodsInfoByGoodsId(goodsId);
    }

    @Override
    public int editAGoodsInfo(GoodsInfo goodsInfo) {
        return goodsInfoMapper.updateAGoodsInfo(goodsInfo);
    }

    /**
     * 根据控制器提供的当前页码和每页显示条数，返回分页数据
     * 1.计算数据起始索引
     * 2.封装分页数据
     * 3.返回分页数据给控制器
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 分页数据
     */
    @Override
    public PageResult queryGoodsInfoListForPagination(Integer pageNum, Integer pageSize) {
        Integer startIndex = (pageNum - 1) * pageSize;

        List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectGoodsInfoListForPagination(startIndex, pageSize);
        int totalCount = goodsInfoMapper.selectCountForPagination();

        PageResult pageResult = new PageResult(pageNum, pageSize, totalCount, goodsInfoList);

        return pageResult;
    }

    @Override
    public int changeGoodsSellStatusByIdsAndStateNum(Long[] ids, Integer status) {
        return goodsInfoMapper.updateGoodsSellStatusByIdsAndStateNum(ids, status);
    }
}
