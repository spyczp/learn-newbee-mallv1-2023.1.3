package ltd.newbee.mall.util;

public class NumberUtil {

    /**
     * 生成订单流水号
     *
     * @return
     */
    public static String genOrderNo(){
        StringBuffer buffer = new StringBuffer(String.valueOf(System.currentTimeMillis()));
        int num = genRandomNum(4);
        buffer.append(num);
        return buffer.toString();
    }

    /**
     * 生成指定长度的随机数
     *
     * @param length
     * @return
     */
    public static int genRandomNum(int length){
        int num = 1;
        for(int i = 0; i < length; i++){
            num = num * 10;
        }
        double random = Math.random();
        if(random < 0.1){
            random += 0.1;
        }
        return (int) (random * num);
    }
}
