package com.idaymay.dzt.common.utils.string;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.*;

/**
 * @author littlehui
 * @version 1.0
 * Xml解析的工具
 * @date 2021/11/15 15:14
 */
public class XmlUtil {

    /**
     * xml转map
     *
     * @param xml
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author littlehui
     * @date 2021/11/15 15:19
     */
    public static Map<String, String> xmlToMap(String xml) throws Exception {
        StringReader reader = new StringReader(xml);
        InputSource source = new InputSource(reader);
        // 创建一个SAXReader对象
        SAXReader sax = new SAXReader();
        // 获取document对象,如果文档无节点，则会抛出Exception提前结束
        Document document = sax.read(source);
        // 获取根节点
        Element root = document.getRootElement();
        // 从根节点开始遍历所有节点
        Map<String, String> map = XmlUtil.getNodes(root);
        return map;
    }

    /**
     * 获取节点并转成Map
     *
     * @param node
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author littlehui
     * @date 2021/11/15 15:20
     */
    public static Map<String, String> getNodes(Element node) {
        Map<String, String> xmlMap = new HashMap<String, String>(10);
        xmlMap.put(node.getName().toLowerCase(), node.getTextTrim());
        // 当前节点的所有属性的list
        List<Attribute> listAttr = node.attributes();
        // 遍历当前节点的所有属性
        for (Attribute attr : listAttr) {
            // 属性名称
            String name = attr.getName();
            // 属性的值
            String value = attr.getValue();
            xmlMap.put(name, value.trim());
        }
        // 递归遍历当前节点所有的子节点
        // 所有一级子节点的list
        List<Element> listElement = node.elements();
        // 遍历所有一级子节点
        for (Element e : listElement) {
            // 递归
            XmlUtil.getNodes(e);
        }
        return xmlMap;
    }

    /**
     * 将xml格式的字符串解析成Map
     *
     * @param xml
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author littlehui
     * @date 2021/11/15 15:28
     */
    public static Map<String, Object> parseXmlStr(String xml)
            throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        return parseElement(root, null);
    }


    /**
     * 从跟节点解析Xml排除excludeNodeNames里面的节点
     *
     * @param root
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author littlehui
     * @date 2021/11/15 15:22
     */
    private static Map<String, Object> parseElement(@NotNull Element root, List<String> excludeNodeNames) {
        excludeNodeNames = excludeNodeNames == null ? Collections.EMPTY_LIST : excludeNodeNames;
        String rootName = root.getName();
        Iterator<Element> rootItor = root.elementIterator();
        Map<String, Object> rMap = new HashMap<>(10);
        List<Map<String, Object>> rList = new ArrayList<>();
        Map<String, Object> rsltMap = null;
        while (rootItor.hasNext()) {
            Element tmpElement = rootItor.next();
            String name = tmpElement.getName();
            if (!excludeNodeNames.contains(name) && !tmpElement.isTextOnly() && rsltMap != null) {
                rList.add(rsltMap);
            }
            if (rsltMap == null) {
                rsltMap = new HashMap<>(10);
            }
            if (!tmpElement.isTextOnly()) {
                Iterator<Element> headItor = tmpElement.elementIterator();
                while (headItor.hasNext()) {
                    Element hElement = headItor.next();
                    if (hElement.isTextOnly()) {
                        rsltMap.put(hElement.getName(), hElement.getText());
                    } else {
                        rsltMap.putAll(parseElement(hElement, excludeNodeNames));
                    }
                }
            }
        }
        rList.add(rsltMap);
        rMap.put(rootName, rList);
        return rMap;
    }

    public static void main(String[] args) throws Exception {
        String xmStr = "<chapters><number>\n" +
                "44\n" +
                "</number>\n" +
                "<title>\n" +
                "批量导入标题\n" +
                "</title>\n" +
                "<content>\n" +
                "批量导入测试内容\n" +
                "</content></chapters>";
        System.out.println(parseXmlStr(xmStr).toString());
    }
}
