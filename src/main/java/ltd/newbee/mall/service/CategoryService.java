package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.util.PageResult;

import java.util.Map;

public interface CategoryService {

    PageResult queryCategoryListForPagination(Map<String, Object> map);

    int createACategory(Category category);
}
