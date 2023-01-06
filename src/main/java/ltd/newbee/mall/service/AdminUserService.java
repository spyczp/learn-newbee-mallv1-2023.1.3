package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.AdminUser;

import java.util.HashMap;

public interface AdminUserService {

    AdminUser queryAdminUserForLogin(String loginUserName, String loginPassword);

    AdminUser queryAdminUserForProfile(Integer loginUserId);

    int editUserInfoAtProfile(AdminUser adminUser);

    int editPasswordAtProfile(HashMap<String, Object> map);
}
