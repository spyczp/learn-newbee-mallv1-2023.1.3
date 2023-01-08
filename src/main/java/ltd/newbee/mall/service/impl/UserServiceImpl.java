package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<User> queryUsersForPagination(Integer startNum, Integer pageSize) {


        return null;
    }

    @Override
    public int queryTotalCountForPagination(Integer startNum, Integer pageSize) {
        return 0;
    }
}
