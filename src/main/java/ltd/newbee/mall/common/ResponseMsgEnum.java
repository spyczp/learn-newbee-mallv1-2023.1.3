package ltd.newbee.mall.common;

public enum ResponseMsgEnum {

    USER_REGISTER_FAIL("用户注册失败"),
    USER_REGISTER_ALREADY_EXIST("用户已存在"),
    USER_REGISTER_ERROR("注册过程出现异常"),
    VERIFY_CODE_WRONG("验证码错误"),
    USER_LOGIN_NOT_EXIST("用户不存在"),
    USER_LOGIN_PASSWORD_WRONG("密码错误，请重新输入")
    ;


    private String message;

    ResponseMsgEnum(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
