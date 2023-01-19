package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Carousel;

import java.util.List;

public interface CarouselMapper {

    List<Carousel> selectCarouselListForPagination(Integer startIndex, Integer pageSize);

    int selectCountForPagination();

    int insertACarousel(Carousel carousel);

    int updateACarousel(Carousel carousel);

    /**
     * 修改isDeleted参数为1
     * @param ids id组成的数组
     * @return 结果数据
     */
    int updateIsDeletedByIds(Integer[] ids, Integer updateUser);

    Carousel selectCarouselById(Integer id);
}
