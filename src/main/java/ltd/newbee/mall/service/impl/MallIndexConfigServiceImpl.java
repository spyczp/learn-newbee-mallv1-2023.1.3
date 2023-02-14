package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.MallIndexConfigMapper;
import ltd.newbee.mall.entity.MallIndexConfig;
import ltd.newbee.mall.service.MallIndexConfigService;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MallIndexConfigServiceImpl implements MallIndexConfigService {

    @Resource
    private MallIndexConfigMapper mallIndexConfigMapper;

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
}
