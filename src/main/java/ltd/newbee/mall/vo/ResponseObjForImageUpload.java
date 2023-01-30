package ltd.newbee.mall.vo;

import lombok.Data;

/**
 * 上传图片后的响应数据
 */
@Data
public class ResponseObjForImageUpload {

    private Integer errno;

    private String message;

    private Object data;
}
