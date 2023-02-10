package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.CarouselMapper;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.NewBeeMallIndexCarouselVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    @Override
    public PageResult queryCarouseListForPagination(Integer pageNum, Integer pageSize) {
        //计算起始数据的下标
        Integer startIndex = (pageNum - 1) * pageSize;
        //访问数据库，获取轮播图数据列表和总记录数
        List<Carousel> carouselList = carouselMapper.selectCarouselListForPagination(startIndex, pageSize);
        int totalCount = carouselMapper.selectCountForPagination();
        //创建PageResult对象，封装数据
        return new PageResult(pageNum, pageSize, totalCount, carouselList);
    }

    @Override
    public int saveACarousel(Carousel carousel) {
        return carouselMapper.insertACarousel(carousel);
    }

    @Override
    public int editACarousel(Carousel carousel) {
        return carouselMapper.updateACarousel(carousel);
    }

    @Override
    public int deleteCarousel(Integer[] ids, Integer updateUser) {
        return carouselMapper.updateIsDeletedByIds(ids, updateUser);
    }

    @Override
    public Carousel queryCarouselById(Integer id) {
        return carouselMapper.selectCarouselById(id);
    }

    /**
     * 到数据库查询轮播图列表
     * 把轮播图数据转为轮播图视图数据
     * @param count 轮播图数量
     * @return 轮播图视图数据列表
     */
    @Override
    public List<NewBeeMallIndexCarouselVO> queryCarouselListByCount(int count) {
        List<NewBeeMallIndexCarouselVO> carouselVOList = new ArrayList<>();

        List<Carousel> carouselList = carouselMapper.selectCarouselListByCount(count);

        if(!CollectionUtils.isEmpty(carouselList)){
            carouselVOList = BeanUtil.copyList(carouselList, NewBeeMallIndexCarouselVO.class);
        }

        return carouselVOList;
    }
}
