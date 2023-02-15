package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.GoodsInfoMapper;
import ltd.newbee.mall.dao.MallIndexConfigMapper;
import ltd.newbee.mall.entity.GoodsInfo;
import ltd.newbee.mall.entity.MallIndexConfig;
import ltd.newbee.mall.service.MallIndexConfigService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.MallIndexConfigGoodsVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallIndexConfigServiceImpl implements MallIndexConfigService {

    @Resource
    private MallIndexConfigMapper mallIndexConfigMapper;

    @Resource
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 访问 数据访问层，查询 商品配置列表数据 和 对应的总条数，
     * 用于前端展示 商品配置信息列表 分页数据
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @param contentType 配置类型
     * @return 分页数据
     */
    @Override
    public PageResult queryListAndCountByConditionForPagination(Integer pageNum, Integer pageSize, Byte contentType) {
        //计算初始数据的索引
        Integer startIndex = (pageNum - 1) * pageSize;
        //访问数据访问层，查询商品配置列表数据 和 对应的总条数
        List<MallIndexConfig> mallIndexConfigList = mallIndexConfigMapper.selectListByConditionForPagination(startIndex, pageSize, contentType);
        int totalCount = mallIndexConfigMapper.selectCountForPagination(contentType);
        //封装数据到分页数据对象
        PageResult pageResult = new PageResult(pageNum, pageSize, totalCount, mallIndexConfigList);

        return pageResult;
    }

    @Override
    public int addAMallIndexConfig(MallIndexConfig mallIndexConfig) {
        return mallIndexConfigMapper.insertAMallIndexConfig(mallIndexConfig);
    }

    @Override
    public int editAMallIndexConfig(MallIndexConfig mallIndexConfig) {
        return mallIndexConfigMapper.updateAMallIndexConfig(mallIndexConfig);
    }

    @Override
    public int deleteMallIndexConfigsByIds(Long[] ids) {
        return mallIndexConfigMapper.updateIsDeletedToOneByIds(ids);
    }

    /**
     * 获取商品视图信息，用于前端首页展示
     * 1.首先根据configType和count查询 商品配置信息列表
     * 2.从 商品配置信息列表 中获取所有对应的商品id
     * 3.根据商品id（goodsId）查询商品列表
     * 4.由商品列表生成商品视图列表
     * 5.返回商品视图列表
     * @param configType 配置类型
     * @param count 查询的数据条数
     * @return 商品视图列表
     */
    @Override
    public List<MallIndexConfigGoodsVO> queryGoodsForIndexShow(Byte configType, Integer count) {
        List<MallIndexConfigGoodsVO> mallIndexConfigGoodsVOList = new ArrayList<>();

        List<MallIndexConfig> mallIndexConfigList = mallIndexConfigMapper.selectByConfigTypeAndCount(configType, count);
        //取出所有的goodsId
        if(!CollectionUtils.isEmpty(mallIndexConfigList)){
            List<Long> goodsIds = mallIndexConfigList.stream().map(MallIndexConfig::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectListByGoodsIds(goodsIds);
            mallIndexConfigGoodsVOList = BeanUtil.copyList(goodsInfoList, MallIndexConfigGoodsVO.class);

            for(MallIndexConfigGoodsVO mallIndexConfigGoodsVO: mallIndexConfigGoodsVOList){
                String goodsName = mallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = mallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if(goodsName.length() > 30){
                    goodsName = goodsName.substring(0, 30) + "...";
                    mallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if(goodsIntro.length() > 22){
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    mallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return mallIndexConfigGoodsVOList;
    }
}
