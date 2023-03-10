package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {

    List<Category> selectCategoryListForPagination(Map<String, Object> map);

    int selectCountForPagination(Map<String, Object> map);

    List<Category> selectCategoryListByParentId(Long parentId);

    Category selectCategoryByCategoryId(Long categoryId);

    int insertACategory(Category category);

    int updateACategory(Category category);

    int updateSomeIsDeletedToOne(Integer[] ids);
}
