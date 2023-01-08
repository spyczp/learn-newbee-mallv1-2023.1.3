package ltd.newbee.mall.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来给前端响应信息的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseObj<T> {

    private Integer code;
    private String message;
    private T data;

    public ResponseObj(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
