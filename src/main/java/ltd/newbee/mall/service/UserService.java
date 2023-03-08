package ltd.newbee.mall.service;


import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.vo.ResponseObj;

public interface UserService {

    ResponseObj addAUser(String loginName, String password) throws Exception;

    User userLogin(String loginName);

    int updateUserInfo(User user);
}
