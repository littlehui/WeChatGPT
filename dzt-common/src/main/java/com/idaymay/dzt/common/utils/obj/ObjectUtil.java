package com.idaymay.dzt.common.utils.obj;

import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;

/**
 * @author littlehui
 * @version 1.0
 * @description TODO
 * @date 2022/4/13 15:57
 */
@Slf4j
public class ObjectUtil {

    /**
     * One of the following conditions isEmpty = true, else = false :
     * 满足下列一个条件则为空<br>
     * 1. null : 空<br>
     * 2. "" or " " : 空串<br>
     * 3. no item in [] or all item in [] are null : 数组中没有元素, 数组中所有元素为空<br>
     * 4. no item in (Collection, Map, Dictionary) : 集合中没有元素<br>
     *
     * @param value
     * @return
     * @author littlehui
     * @date May 6, 2010 4:21:56 PM
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if ((value instanceof String)
                && ((((String) value).trim().length() <= 0) || "null"
                .equalsIgnoreCase((String) value))) {
            return true;
        }
        if ((value instanceof Object[]) && (((Object[]) value).length <= 0)) {
            return true;
        }
        if (value instanceof Object[]) { // all item in [] are null :
            // 数组中所有元素为空
            Object[] t = (Object[]) value;
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    if (t[i] instanceof String) {
                        if (((String) t[i]).trim().length() > 0
                                || "null".equalsIgnoreCase((String) t[i])) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        if ((value instanceof Collection)
                && ((Collection<?>) value).size() <= 0) {
            return true;
        }
        if ((value instanceof Dictionary)
                && ((Dictionary<?, ?>) value).size() <= 0) {
            return true;
        }
        if ((value instanceof Map) && ((Map<?, ?>) value).size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 对象是否是值类型.
     *
     * @param obj
     * @return
     * @author littlehui
     * @date 2012-9-26 下午03:01:44
     */
    public static boolean isValueType(Object obj) {
        if (obj == null || obj instanceof String || obj instanceof Number
                || obj instanceof Boolean || obj instanceof Character
                || obj instanceof Date) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对象是否是值类型.
     *
     * @param obj
     * @return
     * @author qingwu
     * @date 2013-7-9 下午03:01:44
     */
    public static boolean isValueTypeWithoutDate(Object obj) {
        if (obj == null || obj instanceof String || obj instanceof Number
                || obj instanceof Boolean || obj instanceof Character) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对象是否是值类型.
     *
     * @return
     * @author qingwu
     * @date 2013-7-9 下午03:01:44
     */
    @SuppressWarnings("rawtypes")
    public static boolean isValueType(Class rClass) {
        String rType = rClass.getName();
        if ("java.lang.String".equals(rType)) {// 字符串类 型
            return true;
        } else if ("java.lang.Integer".equals(rType) || "int".equals(rType)) {// 整形
            return true;
        } else if ("java.lang.Float".equals(rType) || "float".equals(rType)) {// 浮点型
            return true;
        } else if ("java.lang.Double".equals(rType) || "double".equals(rType)) {// 双精度
            return true;
        } else if ("java.lang.Boolean".equals(rType) || "boolean".equals(rType)) {// 布尔型
            return true;
        } else if ("java.lang.Long".equals(rType) || "long".equals(rType)) {// Long类型
            return true;
        } else if ("java.lang.Short".equals(rType) || "short".equals(rType)) {// Short类型
            return true;
        } else if ("java.sql.Timestamp".equals(rType)) { // Timestamp类型
            return true;
        } else if ("java.util.Date".equals(rType)) { // Date类型
            return true;
        }
        return false;
    }

    /**
     * 对象是否是值类型.
     *
     * @return
     * @author qingwu
     * @date 2013-7-9 下午03:01:44
     */
    @SuppressWarnings("rawtypes")
    public static boolean isValueTypeWithoutDate(Class rClass) {
        String rType = rClass.getName();
        if ("java.lang.String".equals(rType)) {// 字符串类 型
            return true;
        } else if ("java.lang.Integer".equals(rType) || "int".equals(rType)) {// 整形
            return true;
        } else if ("java.lang.Float".equals(rType) || "float".equals(rType)) {// 浮点型
            return true;
        } else if ("java.lang.Double".equals(rType) || "double".equals(rType)) {// 双精度
            return true;
        } else if ("java.lang.Boolean".equals(rType) || "boolean".equals(rType)) {// 布尔型
            return true;
        } else if ("java.lang.Long".equals(rType) || "long".equals(rType)) {// Long类型
            return true;
        } else if ("java.lang.Short".equals(rType) || "short".equals(rType)) {// Short类型
            return true;
        }
        return false;
    }

    /**
     * 是否是集合.
     *
     * @param obj
     * @return
     * @author littlehui
     * @date 2012-9-26 下午03:50:55
     */
    public static boolean isCollection(Object obj) {
        if (obj instanceof Collection<?>) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否是MAP.
     *
     * @param obj
     * @return
     * @author littlehui
     * @date 2013-2-8 下午4:35:30
     */
    public static boolean isMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将map值转化成对象, 不映射集合属性.
     *
     * @param type
     * @return
     * @author littlehui
     * @date 2012-9-26 下午03:39:54
     */
    public static <T> T toBean(Map<String, Object> values, Class<T> type) {
        T obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
            obj = type.newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (values.containsKey(propertyName)) {// 否则注入到与key值一样的字段
                    Object value = values.get(propertyName);
                    Object[] args = new Object[1];
                    args[0] = value;
                    descriptor.getWriteMethod().invoke(obj, args);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个 Map.
     *
     * @param bean
     * @return
     * @author littlehui
     * @date 2012-9-26 下午03:40:56
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> returnMap;
        try {
            Class<?> type = bean.getClass();
            returnMap = new HashMap<String, Object>();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
            for (int i = 0; i< propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if(ObjectUtil.isValueType(result) || result == null){
                        returnMap.put(propertyName, result);
                    }else if(ObjectUtil.isCollection(result)){
                        Collection<?> collectionResult = (Collection<?>)result;
                        Collection collection = (Collection) result.getClass().newInstance();
                        for (Object o : collectionResult) {
                            if(ObjectUtil.isValueType(o) || o == null){
                                collection.add(o);
                            }else{
                                collection.add(toMap(o));
                            }
                        }
                        returnMap.put(propertyName, collection);
                    }else if(result.getClass().isArray()){
                        throw new RuntimeException("bean property can't be array");
                    }else{ //自定义对象
                        returnMap.put(propertyName, toMap(result));
                    }
                }
            }
        } catch (Exception e) {
            RuntimeException ex = new RuntimeException("convent object to map error");
            ex.initCause(e);
            throw ex;
        }
        return returnMap;
    }

    /**
     * 纠正源对象的值类型，与目标值类型一致.
     *
     * @param sObj
     *            源类对象
     *            目标类型
     * @return 纠正后的正确的对象
     * @author qingwu
     * @date 2013-3-29 下午5:16:38
     */
    public static <T> Object correctObjValue(Object sObj, Class<T> rClass) {
        Object rObj = sObj;
        if (sObj == null) {// 源对象是null，返回源对象
            return rObj;
        }
        if (!sObj.getClass().getName().equals(rClass.getName())) {// 如果源类型与目标类型不一致
            if (sObj instanceof String) {// 对源类型是String类型的值进行纠正
                String value = sObj.toString();
                String rType = rClass.getName();
                if ("java.lang.Integer".equals(rType) || "int".equals(rType)) {// 整形
                    rObj = Integer.parseInt(value);
                } else if ("java.lang.Float".equals(rType)
                        || "float".equals(rType)) {// 浮点型
                    rObj = Float.parseFloat(value);
                } else if ("java.lang.Double".equals(rType)
                        || "double".equals(rType)) {// 双精度
                    rObj = Double.parseDouble(value);
                } else if ("java.lang.Boolean".equals(rType)
                        || "boolean".equals(rType)) {// 布尔型
                    rObj = Boolean.parseBoolean(value);
                } else if ("java.lang.Long".equals(rType)
                        || "long".equals(rType)) {// Long类型
                    rObj = Long.parseLong(value);
                } else if ("java.lang.Short".equals(rType)
                        || "short".equals(rType)) {// Short类型
                    rObj = Short.parseShort(value);
                } else if ("java.sql.Timestamp".equals(rType)) { // Timestamp类型
                    rObj = Timestamp.valueOf(value);
                } else if ("java.util.Date".equals(rType)) { // Date类型
                    DateFormat df = DateFormat.getDateInstance();
                    try {
                        rObj = df.parseObject(value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return rObj;
    }
    public static <M,T> List<T> convertList(List<M> objList , Converter<M,T> converter) {
        List<T> list = new ArrayList<T>();
        if (objList == null) {
            return list;
        }
        for (M m : objList) {
            list.add(converter.convert(m));
        }
        return list;
    }

    public static interface  Converter<H,Q>{
        public Q convert(H h);
    }

    public static <M, T> T convertObj(M obj, Class<T> clazz) {
        try {
            Map<String, Object> map = BeanUtils.convertBean(obj);
            T resultObject = BeanUtils.convertMap(clazz, map);
            return resultObject;
        } catch (Exception var4) {
            throw new RuntimeException("convert obj error! source class :" + obj.getClass() + ",target :" + clazz);
        }
    }
}
