package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.common.ResponseMsgEnum;
import ltd.newbee.mall.dao.UserMapper;
import ltd.newbee.mall.entity.User;
import ltd.newbee.mall.service.UserService;
import ltd.newbee.mall.util.MD5Util;
import ltd.newbee.mall.util.ResponseGenerator;
import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    /**
     * 新增用户
     * 1.需要判断该用户是否存在，根据loginName判断
     * 2.封装用户数据
     * 3.访问数据库，新增用户信息
     * @param loginName 登录用户名
     * @param password 密码
     * @return 响应对象，包含注册结果
     */
    @Override
    public ResponseObj addAUser(String loginName, String password) throws Exception {
        //1.需要判断该用户是否存在，根据loginName判断
        if (userMapper.selectUserInfoByLoginName(loginName) != null) {
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.USER_REGISTER_ALREADY_EXIST.getMessage());
        }

        //把用户提交的密码转为md5形式
        String passwordMd5 = MD5Util.getMD5(password);
        //2.封装数据
        User user = User.builder()
                .loginName(loginName)
                .nickName(loginName)
                .passwordMd5(passwordMd5)
                .isDeleted((byte) 0)
                .lockedFlag((byte) 0)
                .createTime(new Date())
                .build();
        //3.访问数据库，新增用户信息
        int result = userMapper.insertAUser(user);
        if(result == 1){
            //成功
            return ResponseGenerator.genSuccessResponse();
        }else{
            return ResponseGenerator.genFailResponse(ResponseMsgEnum.USER_REGISTER_FAIL.getMessage());
        }
    }

    @Override
    public User userLogin(String loginName){
        return userMapper.selectUserInfoByLoginName(loginName);
    }

    @Override
    public int updateUserInfo(User user) {
        return userMapper.updateUserInfo(user);
    }
}
