package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {

    List<Category> selectCategoryListForPagination(Map<String, Object> map);

    int selectCountForPagination(Map<String, Object> map);

    int insertACategory(Category category);
}
