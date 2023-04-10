package com.baomidou.mybatisplus.core;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/7/26 11:55
 */
@Slf4j
public class ClassUtil {

    private final static Map<String,Field> fieldCache =  new ConcurrentHashMap<String,Field>();

    public static boolean hasProperty(Class clz, String propertyName){
        try {
            Field f = getField(clz, propertyName,false);
            if (f != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("获取属性异常" , e) ;
        }
        return false;
    }

    public static Field getField(Class clz, String fieldName) {
        return getField(clz, fieldName, true);
    }

    public static Field getField(Class clz, String fieldName, boolean exception) {
        String key = clz.getName()+" - " +fieldName;
        Field f = fieldCache.get(key);
        if (f != null) {
            return f;
        }

        for (; clz != Object.class ; clz = clz.getSuperclass()){
            try {
                if (!Object.class.getName().equals(clz.getName())) {
                    Field field = clz.getDeclaredField(fieldName);
                    fieldCache.put(key, field);
                    return field;
                }
            } catch (NoSuchFieldException e) {
                //DO NOTHING
            }
        }

        if(exception){
            throw new RuntimeException("no such field in " + clz.getName());
        }
        return null;
    }

}
