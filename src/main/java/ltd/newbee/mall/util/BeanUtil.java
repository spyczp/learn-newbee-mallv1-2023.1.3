package ltd.newbee.mall.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static <T> List<T> copyList(List sources, Class<T> clazz){
        return copyList(sources, clazz, null);
    }

    private static <T> Object copyProperties(Object source, T target, String... ignoreProperties) {
        if(source == null){
            return target;
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    private static <T> List<T> copyList(List sources, Class<T> clazz, Callback<T> callback) {
        ArrayList<T> targetList = new ArrayList<>();
        if(sources != null){
            try{
                for(Object source: sources){
                    T target = clazz.newInstance();
                    copyProperties(source, target);
                    if(callback != null){
                        callback.set(source, target);
                    }
                    targetList.add(target);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetList;
    }

    public static interface Callback<T> {
        void set(Object source, T target);
    }
}
