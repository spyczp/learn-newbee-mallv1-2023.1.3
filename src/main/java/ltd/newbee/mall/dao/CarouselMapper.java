package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Carousel;

import java.util.List;

public interface CarouselMapper {

    List<Carousel> selectCarouselListForPagination(Integer startIndex, Integer pageSize);

    int selectCountForPagination();
}
