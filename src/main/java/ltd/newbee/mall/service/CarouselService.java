package ltd.newbee.mall.service;

import ltd.newbee.mall.util.PageResult;

public interface CarouselService {

    PageResult queryCarouseListForPagination(Integer pageNum, Integer pageSize);
}
