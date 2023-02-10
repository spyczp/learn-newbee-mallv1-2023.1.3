package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.vo.NewBeeMallIndexCarouselVO;

import java.util.List;

public interface CarouselService {

    PageResult queryCarouseListForPagination(Integer pageNum, Integer pageSize);

    int saveACarousel(Carousel carousel);

    int editACarousel(Carousel carousel);

    int deleteCarousel(Integer[] ids, Integer updateUser);

    Carousel queryCarouselById(Integer id);

    List<NewBeeMallIndexCarouselVO> queryCarouselListByCount(int count);
}
