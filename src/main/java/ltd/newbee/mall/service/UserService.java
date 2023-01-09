package ltd.newbee.mall.service;


import ltd.newbee.mall.util.PageResult;

public interface UserService {

    PageResult queryUsersForPagination(Integer startNum, Integer pageSize);
}
