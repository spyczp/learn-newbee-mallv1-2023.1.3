package ltd.newbee.mall.util;

import lombok.Data;

/**
 * 图片数据，用于上传图片后，响应给前端的图片信息
 * "url": "xxx", // 图片 src ，必须
 * "alt": "yyy", // 图片描述文字，非必须
 * "href": "zzz" // 图片的链接，非必须
 */
@Data
public class ImageData {

    private String url;

    private String alt;

    private String href;
}
