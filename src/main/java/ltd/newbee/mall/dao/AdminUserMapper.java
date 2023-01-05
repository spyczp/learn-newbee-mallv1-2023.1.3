package ltd.newbee.mall.dao;

import ltd.newbee.mall.entity.AdminUser;

public interface AdminUserMapper {

    AdminUser selectAdminUserForLogin(String loginUserName, String loginPassword);
}
