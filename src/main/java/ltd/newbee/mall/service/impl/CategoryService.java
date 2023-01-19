package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.util.PageResult;

import java.util.Map;

public interface CategoryService {

    PageResult queryCategoryListForPagination(Map<String, Object> map);
}
