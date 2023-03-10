package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.dao.AdminUserMapper;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.service.AdminUserService;
import ltd.newbee.mall.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser queryAdminUserForLogin(String loginUserName, String loginPassword) {
        String passwordMD5 = MD5Util.getMD5(loginPassword);
        return adminUserMapper.selectAdminUserForLogin(loginUserName, passwordMD5);
    }

    @Override
    public AdminUser queryAdminUserForProfile(Integer loginUserId) {
        return adminUserMapper.selectAdminUserForProfile(loginUserId);
    }

    @Override
    public int editUserInfoAtProfile(AdminUser adminUser) {
        AdminUser adminUser1 = adminUserMapper.selectAdminUserForProfile(adminUser.getAdminUserId());
        if(adminUser1 == null){
            return 0;
        }
        return adminUserMapper.updateUserInfoAtProfile(adminUser);
    }

    @Override
    public int editPasswordAtProfile(HashMap<String, Object> map) {
        return adminUserMapper.updatePasswordAtProfile(map);
    }
}
