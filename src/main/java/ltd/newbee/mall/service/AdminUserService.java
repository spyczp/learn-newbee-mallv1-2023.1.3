package ltd.newbee.mall.service;

import ltd.newbee.mall.entity.AdminUser;

public interface AdminUserService {

    AdminUser queryAdminUserForLogin(String loginUserName, String loginPassword);
}
