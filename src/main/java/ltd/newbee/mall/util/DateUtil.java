package ltd.newbee.mall.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     *
     * @return 获取格式化后的当前日期
     */
    public static String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(new Date());
        return nowDate;
    }

    /**
     *
     * @return 获取格式化后的当前时间
     */
    public static String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String nowTime = sdf.format(new Date());
        return nowTime;
    }

    /**
     *
     * @return 获取格式化后的当前日期+时间
     */
    public static String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(new Date());
        return dateTime;
    }
}
