package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.CategoryMapper;
import ltd.newbee.mall.entity.Category;
import ltd.newbee.mall.service.CategoryService;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public PageResult queryCategoryListForPagination(Map<String, Object> map) {
        /*
        * 需要返回给前端的数据：
        *   1.分类列表
        *   2.总记录数
        * */
        List<Category> categoryList = categoryMapper.selectCategoryListForPagination(map);
        int totalCount = categoryMapper.selectCountForPagination(map);
        Integer currentPage = (Integer) map.get("pageNum");
        Integer pageSize = (Integer) map.get("pageSize");
        PageResult pageResult = new PageResult(currentPage, pageSize, totalCount, categoryList);

        return pageResult;
    }

    @Override
    public int createACategory(Category category) {
        return categoryMapper.insertACategory(category);
    }

    @Override
    public int editACategory(Category category) {
        return categoryMapper.updateACategory(category);
    }

    @Override
    public int editSomeIsDeletedToOne(Integer[] ids) {
        return categoryMapper.updateSomeIsDeletedToOne(ids);
    }

    @Override
    public List<Category> queryCategoryListByParentId(Long parentId) {
        return categoryMapper.selectCategoryListByParentId(parentId);
    }

    @Override
    public Category queryCategoryByCategoryId(Long categoryId) {
        return categoryMapper.selectCategoryByCategoryId(categoryId);
    }
}
