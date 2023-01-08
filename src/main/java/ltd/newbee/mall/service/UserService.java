package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.User;

import java.util.List;

public interface UserService {

    List<User> queryUsersForPagination(Integer startNum, Integer pageSize);

    int queryTotalCountForPagination(Integer startNum, Integer pageSize);
}
