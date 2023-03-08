package ltd.newbee.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Order {

    private Long orderId;
    private String orderNo;
    private Long userId;
    private Integer totalPrice;
    private Byte payStatus;
    private Byte payType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    private Byte orderStatus;
    private String extraInfo;
    private String userName;
    private String userPhone;
    private String userAddress;
    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
