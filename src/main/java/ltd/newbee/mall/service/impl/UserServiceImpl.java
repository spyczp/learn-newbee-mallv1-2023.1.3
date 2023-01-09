package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 查询用户列表和总记录数用于分页功能
     * @param startNum 起始数据
     * @param pageSize 每页显示条数
     * @return 分页数据对象
     */
    @Override
    public PageResult queryUsersForPagination(Integer startNum, Integer pageSize) {

        List<User> userList = userMapper.selectUsersForPagination(startNum, pageSize);
        int totalCount = userMapper.selectTotalCountForPagination();

        //当前页没有传到业务层，需要在控制器那里设置
        return new PageResult(0, pageSize, totalCount, userList);
    }

}
