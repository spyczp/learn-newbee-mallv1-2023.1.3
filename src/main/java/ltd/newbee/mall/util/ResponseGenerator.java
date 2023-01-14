package ltd.newbee.mall.util;

import ltd.newbee.mall.vo.ResponseObj;
import org.springframework.util.StringUtils;

public class ResponseGenerator {

    private static final int RESPONSE_CODE_SUCCESS = 0;
    private static final int RESPONSE_CODE_FAIL = 1;
    private static final String RESPONSE_MESSAGE_SUCCESS = "success";
    private static final String RESPONSE_MESSAGE_FAIL = "fail";

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
     * 生成成功的响应信息-带数据
     * @param data 响应数据
     * @return 响应信息对象
     */
    public static ResponseObj genSuccessResponse(Object data){
        ResponseObj<PageResult> responseObj = new ResponseObj<>();
        responseObj.setCode(RESPONSE_CODE_SUCCESS);
        responseObj.setMessage(RESPONSE_MESSAGE_SUCCESS);
        responseObj.setData((PageResult) data);
        return responseObj;
    }
}
