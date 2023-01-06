package ltd.newbee.mall.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUser {
    private Integer adminUserId;
    private String loginUserName;
    private String loginPassword;
    private String nickName;
    private Integer locked;
}
