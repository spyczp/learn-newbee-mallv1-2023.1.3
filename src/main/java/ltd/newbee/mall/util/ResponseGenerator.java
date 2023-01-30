package ltd.newbee.mall.util;

import ltd.newbee.mall.vo.ResponseObj;
import ltd.newbee.mall.vo.ResponseObjForImageUpload;
import org.springframework.util.StringUtils;

public class ResponseGenerator {

    private static final int RESPONSE_CODE_SUCCESS = 0;
    private static final int RESPONSE_CODE_FAIL = 1;
    private static final String RESPONSE_MESSAGE_SUCCESS = "success";
    private static final String RESPONSE_MESSAGE_FAIL = "fail";

    //上传图片成功与否使用的响应码
    private static final int RESPONSE_ERRNO_SUCCESS = 0;
    private static final int RESPONSE_ERRNO_FAIL = 1;

    /**
     *{
     *   "errno": 1, // 只要不等于 0 就行
     *   "message": "失败信息"
     * }
     * @return 上传图片失败的响应数据
     */
    public static ResponseObjForImageUpload genFailResponse(){
        ResponseObjForImageUpload r = new ResponseObjForImageUpload();
        r.setErrno(RESPONSE_ERRNO_FAIL);
        r.setMessage(RESPONSE_MESSAGE_FAIL);
        return r;
    }

    /**
     *
     * {
     *     "errno": 0, // 注意：值是数字，不能是字符串
     *     "data": {
     *         "url": "xxx", // 图片 src ，必须
     *         "alt": "yyy", // 图片描述文字，非必须
     *         "href": "zzz" // 图片的链接，非必须
     *     }
     * }
     * @param data 图片信息
     * @return 上传图片成功的响应数据
     */
    public static ResponseObjForImageUpload genSuccessResponse(ImageData data){
        ResponseObjForImageUpload r = new ResponseObjForImageUpload();
        r.setErrno(RESPONSE_ERRNO_SUCCESS);
        r.setMessage(RESPONSE_MESSAGE_SUCCESS);
        r.setData(data);
        return r;
    }

    /**
     * 生成失败的响应信息
     * @param message 响应的文字信息
     * @return 响应信息对象
     */
    public static ResponseObj genFailResponse(String message){
        ResponseObj responseObj = new ResponseObj<>();
        responseObj.setCode(RESPONSE_CODE_FAIL);
        if(StringUtils.hasText(message)){
            responseObj.setMessage(message);
        }else{
            responseObj.setMessage(RESPONSE_MESSAGE_FAIL);
        }
        return responseObj;
    }

    /**
     * 生成成功的默认响应信息
     * @return 响应数据
     */
    public static ResponseObj genSuccessResponse(){
        ResponseObj responseObj = new ResponseObj();
        responseObj.setCode(RESPONSE_CODE_SUCCESS);
        responseObj.setMessage(RESPONSE_MESSAGE_SUCCESS);
        return responseObj;
    }

    /**
     * 生成成功的响应信息-带数据
     * @param data 响应数据
     * @return 响应信息对象
     */
    public static <T> ResponseObj genSuccessResponse(T data){
        ResponseObj<T> responseObj = new ResponseObj<>();
        responseObj.setCode(RESPONSE_CODE_SUCCESS);
        responseObj.setMessage(RESPONSE_MESSAGE_SUCCESS);
        responseObj.setData(data);
        return responseObj;
    }
}
