package com.baomidou.mybatisplus.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/7/26 11:54
 */
public class ObjectUtil {

    public static <T> T setProperty(T t, String propertyName, Object property) {
        try {
            PropertyDescriptor propertyDescriptor = getProperty(Introspector.getBeanInfo(t.getClass()), propertyName);
            Method method = propertyDescriptor.getWriteMethod();
            method.invoke(t, property);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static PropertyDescriptor getProperty(BeanInfo beanInfo, String property) {
        PropertyDescriptor[] propertys = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertys) {
            if (propertyDescriptor.getName().equals(property) && !"class".equals(property)) {
                return propertyDescriptor;
            }
        }
        return null;
    }
}
