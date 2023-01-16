package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageResult;

public interface CarouselService {

    PageResult queryCarouseListForPagination(Integer pageNum, Integer pageSize);

    int saveACarousel(Carousel carousel);

    int editACarousel(Carousel carousel);

    int deleteCarousel(Integer[] ids, Integer updateUser);
}
