package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.MallIndexConfig;

import java.util.List;

public interface MallIndexConfigMapper {

    /**
     * 根据configId删除商城配置信息
     * 实际是修改每条商城配置信息的is_deleted的值为1
     * @param ids configId组成的数组
     * @return 影响行数
     */
    int updateIsDeletedToOneByIds(Long[] ids);

    /**
     * 修改一条 商城首页配置 数据
     * @param mallIndexConfig 商城首页配置 数据
     * @return 影响行数
     */
    int updateAMallIndexConfig(MallIndexConfig mallIndexConfig);

    /**
     * 新增一条 商城首页配置 数据
     * @param mallIndexConfig 商城首页配置 数据
     * @return 影响行数
     */
    int insertAMallIndexConfig(MallIndexConfig mallIndexConfig);

    /**
     * 查询 首页配置数据 列表，用于分页展示
     * @param startIndex 开始数据索引
     * @param pageSize 查询记录数
     * @param configType 配置类型，作为查询的过滤条件
     * @return 首页配置数据 列表
     */
    List<MallIndexConfig> selectListByConditionForPagination(Integer startIndex,
                                                                        Integer pageSize,
                                                                        Byte configType);

    /**
     * 查询 首页配置数据 数量
     * @param configType 配置类型，作为查询的过滤条件
     * @return 总的 首页配置数据 条数
     */
    int selectCountForPagination(Byte configType);
}
