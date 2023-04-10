package com.idaymay.dzt.common.utils.obj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/7/27 15:41
 */
public class BeanUtils {


    private final static Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    public static Map convertBean(Object bean)   {
        if (bean == null) {
            return new HashMap();
        }

        Class type = bean.getClass();
        Map returnMap = new HashMap();

        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = null;
                try {
                    result = readMethod.invoke(bean, new Object[0]);
                } catch (Exception e) {
                    logger.error("bean:"+bean.getClass()+"转换 map:失败,字段set value 失败:"+propertyName );
                }
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }

    public static <T> List<Map<String, Object>> covertBeans(List<T> beans) {
        List<Map<String, Object>> rtMaps = new ArrayList();
        for (T bean : beans) {
            Map<String, Object> map = convertBean(bean);
            rtMaps.add(map);
        }
        return rtMaps;
    }

    @SuppressWarnings("rawtypes")
    public static <R> R  convertMap(Class<R> type, Map map) {
        // 获取类属性
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        // 创建 JavaBean 对象
        Object obj = null;
        try {
            obj = type.newInstance();
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
            for (int i = 0; i< propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();

                if (map.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    try {
                        Object value = map.get(propertyName);
                        Object[] args = new Object[1];
                        args[0] = value;
                        descriptor.getWriteMethod().invoke(obj, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return (R)obj;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R)obj;
    }

    public static <T> List<T> converMaps2Object(List<Map<String, Object>> datas, Class<T> beanClass) {
        List<T> retList = new ArrayList();
        for (Map map : datas) {
            T t = convertMap(beanClass, map);
            retList.add(t);
        }
        return retList;
    }
}
