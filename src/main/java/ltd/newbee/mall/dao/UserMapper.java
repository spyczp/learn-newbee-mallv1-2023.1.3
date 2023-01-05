package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.User;

import java.util.List;

public interface UserMapper {

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
