package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.User;


public interface UserMapper {

    int insertAUser(User user);

    User selectUserInfoByLoginName(String loginName);

    int updateUserInfo(User user);
}
