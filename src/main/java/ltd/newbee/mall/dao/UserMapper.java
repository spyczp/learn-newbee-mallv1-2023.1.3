package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.User;

import java.util.List;

public interface UserMapper {

    /**
     * 查询列表数据，用于分页功能
     * @param startNum 起始数据
     * @param pageSize 限制查询记录数
     * @return 用户列表
     */
    List<User> selectUsersForPagination(Integer startNum, Integer pageSize);

    /**
     * 查询总记录数，用于分页功能
     * @return 总记录数
     */
    int selectTotalCountForPagination();

    /**
     * 返回数据列表
     * @return
     */
    List<User> findAllUser();

    /**
     * 新增用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 修改
     * @param user
     * @return
     */
    int updateUser(User user);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteUser(Integer id);
}
