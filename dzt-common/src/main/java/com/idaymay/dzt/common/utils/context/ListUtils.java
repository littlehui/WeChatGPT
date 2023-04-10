package com.idaymay.dzt.common.utils.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * List工具
 * @author littlehui
 * @date 2021/11/15 14:44
 * @version 1.0
 */
public class ListUtils {

	/**
	 * 获取list里面单个对象的单个属性重新组装成一个list
	 * @param list 原始列表
     * @param columnName 字段名
     * @param columnClass 字段类型
	 * @author littlehui
	 * @date 2021/11/15 15:45
	 * @return java.util.List<T>
	 */
	public static <T> List<T> getListItemsSingleColumnList(List list, String columnName, Class<T> columnClass) {
		List<T> returnList = new ArrayList<T>();
		if (!ListUtils.isEmpty(list)) {
			for (Object object : list) {
				Object columnObject = getPropValueByName(object, columnName);
				if (columnObject != null) {
					returnList.add((T) columnObject);
				}
			}
		}
		return returnList;
	}

	/**
	 * 获取字段属性值
	 * @param object 原始对象
     * @param propName 属性名
	 * @author littlehui
	 * @date 2021/11/15 15:44
	 * @return java.lang.Object
	 */
	public static Object getPropValueByNameSimple(Object object, String propName) {
		try {
			// 优先从方法获取，get+属性(属性第一个字母为转换成大写)
			Object result = getPropValueByMethod(object, propName);
			if (result == null) {
				Field field = null;
				try {
					field = object.getClass().getDeclaredField(propName);
				} catch (NoSuchFieldException e) {
					if (object.getClass().getSuperclass() != null) {
						field = object.getClass().getSuperclass().getDeclaredField(propName);
					}
				}
				if (field == null) {
					return null;
				}
				// 获取原来的访问控制权限
				boolean accessFlag = field.isAccessible();
				// 修改访问控制权限
				field.setAccessible(true);
				result = field.get(object);
				field.setAccessible(accessFlag);
			}
			return result;
		} catch (Exception e) {
			// 异常返回空
			return getPropValueByMethod(object, propName);
		}
	}

	/**
	 * 根据属性名称获取属性值.
	 * @param object 原始对象
     * @param propName 属性名
	 * @author littlehui
	 * @date 2021/11/15 15:45
	 * @return java.lang.Object
	 */
	public static Object getPropValueByName(Object object, String propName) {
		try {
			String[] props = propName.split("\\.");
			Object o = object;
			for (String prop : props) {
				o = getPropValueByNameSimple(o, prop);
				if (o == null) {
					break;
				}
			}
			return o;
		} catch (Exception e) {
			// 异常返回空
			return null;
		}
	}

	/**
	 * 根据属性名称获取属性值.
	 * @param object 原始对象
     * @param propName 属性名
	 * @author littlehui
	 * @date 2021/11/15 15:45
	 * @return java.lang.Object
	 */
	public static Object getPropValueByMethod(Object object, String propName) {
		try {
			StringBuffer sb = new StringBuffer(propName);
			sb.setCharAt(0, Character.toUpperCase(propName.charAt(0)));

			Method method = object.getClass().getDeclaredMethod("get" + sb.toString());
			if (method == null) {
				return null;
			}
			// 获取原来的访问控制权限
			boolean accessFlag = method.isAccessible();
			// 修改访问控制权限
			method.setAccessible(true);
			Object result = method.invoke(object);
			method.setAccessible(accessFlag);
			return result;
		} catch (Exception e) {
			// 异常返回空
			return null;
		}
	}

	/**
	 * 判断容器是否为空
	 * @param c 容器
	 * @author littlehui
	 * @date 2021/11/15 15:45
	 * @return boolean
	 */
	public static boolean isEmpty(Collection c) {
		if (c == null || c.size() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 是否不为空
	 * @param c 需要判断的容器
	 * @author littlehui
	 * @date 2021/11/15 15:46
	 * @return boolean
	 */
	public static boolean isNotEmpty(Collection c) {
		return !isEmpty(c);
	}

	/**
	 * 拼装list成String
	 * @param list 原始列表
     * @param separator 分隔符
	 * @author littlehui
	 * @date 2021/11/15 15:46
	 * @return java.lang.String
	 */
	public static String list2String(List list, String separator) {
		StringBuffer returnStr = new StringBuffer("");
		if (isNotEmpty(list)) {
			int i = 0;
			for (Object o : list) {
				returnStr.append(o);
				i++;
				if (i < list.size()) {
					returnStr.append(separator);
				}
			}
		}
		return returnStr.toString();
	}

	/**
	 * 从给定的List数组中随机返回特定的数量
	 * @param list 被随机的列表
	 * @param selected 随机条数
	 * @author littlehui
	 * @date 2021/11/15 16:09
	 * @return java.util.List<T>
	 */
	public static <T> List<T> randSubList(List<T> list, int selected) {
		List<T> reList = new ArrayList<T>();
		Random random = new Random();
		// 先抽取，备选数量的个数
		if (list.size() >= selected) {
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		} else {
			selected = list.size();
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		}
		return reList;
	}
}
