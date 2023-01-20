package ltd.newbee.mall.service;

import ltd.newbee.mall.util.PageResult;

import java.util.Map;

public interface CategoryService {

    PageResult queryCategoryListForPagination(Map<String, Object> map);
}
