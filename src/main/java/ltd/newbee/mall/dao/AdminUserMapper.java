package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.AdminUser;

import java.util.HashMap;

public interface AdminUserMapper {

    AdminUser selectAdminUserForLogin(String loginUserName, String loginPassword);

    AdminUser selectAdminUserForProfile(Integer loginUserId);

    int updateUserInfoAtProfile(AdminUser adminUser);

    int updatePasswordAtProfile(HashMap<String, Object> map);
}
