package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.service.GoodsInfoService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.MallSearchGoodsVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 通过关键字或者分类搜索商品列表，在搜索结果页面展示商品列表
     * 1.计算startIndex
     * 2.访问数据库，查询商品列表和总数据量
     *      把 商品列表数据 转换成 商品视图列表数据
     * 3.封装分页数据
     * 4.返回分页数据给调用者
     * @param map 前端提交的参数
     * @return 分页数据和商品列表
     */
    @Override
    public PageResult searchGoodsInfoListForMall(Map<String, Object> map) {
        Integer pageNum = (Integer) map.get("pageNum");
        Integer pageSize = (Integer) map.get("pageSize");
        Integer startIndex = (pageNum - 1) * pageSize;
        map.put("startIndex", startIndex);

        List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectGoodsInfoListByConditionForMall(map);
        int totalCount = goodsInfoMapper.selectCountForMall(map);

        List<MallSearchGoodsVO> goodsInfoVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(goodsInfoList)){
            goodsInfoVOList = BeanUtil.copyList(goodsInfoList, MallSearchGoodsVO.class);
            for(MallSearchGoodsVO mallSearchGoodsVO: goodsInfoVOList){
                String goodsName = mallSearchGoodsVO.getGoodsName();
                String goodsIntro = mallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if(goodsName.length() > 28){
                    goodsName = goodsName.substring(0, 28) + "...";
                    mallSearchGoodsVO.setGoodsName(goodsName);
                }
                if(goodsIntro.length() > 30){
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    mallSearchGoodsVO.setGoodsName(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(pageNum, pageSize, totalCount, goodsInfoVOList);
        return pageResult;
    }
}
