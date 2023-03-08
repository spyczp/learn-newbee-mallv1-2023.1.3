package ltd.newbee.mall.exception;

public class MallException extends RuntimeException{

    public MallException(){

    }

    public MallException(String message){
        super(message);
    }

    /**
     * 抛出一个异常
     * @param message 异常信息
     */
    public static void fail(String message){
        throw new MallException(message);
    }
}
