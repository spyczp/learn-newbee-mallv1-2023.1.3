package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.CarouselMapper;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.service.CarouselService;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
}
