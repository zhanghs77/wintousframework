package com.ctg.itrdc.event.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author zengke
 * 
 */
public class Dom4jUtils {
    
    @SuppressWarnings({"rawtypes", "unchecked" })
    public static List getChilds(Node srcNode) {
        List childNodes = null;
        if (srcNode instanceof Document) {
            childNodes = new ArrayList();
            childNodes.add(((Document) srcNode).getRootElement());
        } else if (srcNode instanceof Element) {
            childNodes = ((Element) srcNode).elements();
        } else {
            childNodes = Collections.emptyList();
        }
        return childNodes;
    }
    
    public static Element findParentElement(Element targetNode, String innerXpath) {
        Element p = targetNode.getParent();
        if (p == null) {
            return null;
        }
        if (p.getPath().equals(innerXpath)) {
            return p;
        } else {
            return findParentElement(p, innerXpath);
        }
    }
    
    public static Element getRootParent(Element e) {
        Element p;
        Element tmp;
        for (p = e; (tmp = p.getParent()) != null; p = tmp) {
        }
        return p;
    }
    
    public static Element createMultiParentRecur(Element targetElement, String innerXPath) {
        String path = targetElement.getPath();
        String extraPath = innerXPath.substring(path.length() + 1, innerXPath.length());
        String paths[] = extraPath.split("/");
        Element currentElement = targetElement;
        for (int i = 0; i < paths.length; i++) {
            Element tmpElement = (Element) currentElement.selectSingleNode(paths[i]);
            if (tmpElement == null) {
                tmpElement = currentElement.addElement(paths[i]);
            }
            currentElement = tmpElement;
        }
        
        return currentElement;
    }
    
    public static Element createNodeRecur(String xPath) {
        String paths[] = xPath.split("/");
        Element parentElement = null;
        Element currentElement = null;
        for (int i = 0; i < paths.length; i++) {
            if (StringUtils.isBlank(paths[i])) {
                continue;
            }
            currentElement = DocumentHelper.createElement(paths[i]);
            if (parentElement != null) {
                parentElement.add(currentElement);
            }
            parentElement = currentElement;
        }
        
        return currentElement;
    }
    
    public static Element createElement(String innerCode, String text) {
        int lastIndex = innerCode.lastIndexOf("/");
        Element e = null;
        if (lastIndex < 0) {
            e = DocumentHelper.createElement(innerCode);
        } else {
            e = createNodeRecur(innerCode);
        }
        e.setText(text);
        return e;
    }
    
    /**
     * 
     * @param fileName
     * @return Dom4j的Document
     * @throws DocumentException
     */
    public static Document read(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));
        return document;
    }
    
    /**
     * 格式化输出
     * 
     * @param node
     * @return
     * @throws IOException
     */
    public static String formatPrettyString(Node node) throws IOException {
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(sw, format);
        writer.write(node);
        writer.close();
        return sw.toString();
    }
    
    /**
     * 紧凑输出
     * 
     * @param node
     * @return
     * @throws IOException
     */
    public static String formatCompactString(Node node) throws IOException {
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createCompactFormat();
        XMLWriter writer = new XMLWriter(sw, format);
        writer.write(node);
        writer.close();
        return sw.toString();
    }
    
    /**
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    public static Document parseText(String xml) throws Exception {
        
        try {
            return DocumentHelper.parseText(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("parse xml text error", e);
        }
        
    }
    
    /**
     * 
     * @param doc
     * @param path
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static List<Node> getNodes(Document doc, String path) throws Exception {
        return (List<Node>) doc.selectNodes(path);
    }
    
    /**
     * 
     * @param doc
     * @param path
     * @return
     * @throws Exception
     */
    public static String getNodesText(Document doc, String path, String split) throws Exception {
        StringBuffer sb = new StringBuffer();
        List<Node> nodeList = getNodes(doc, path);
        
        if (nodeList == null || nodeList.size() == 0) {
            return null;
        }
        int index = 0;
        Iterator<?> it = nodeList.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Element element = (Element) node;
            if (index > 0) {
                sb.append(split);
            }
            sb.append(element.getTextTrim());
            index++;
        }
        
        return sb.toString();
        
    }
    
    /**
     * 
     * @param doc
     * @param path
     * @return
     * @throws Exception
     */
    public static Node getNode(Document doc, String path) throws Exception {
        return doc.selectSingleNode(path);
    }
    
    /**
     * 
     * @param doc
     * @param path
     * @return
     * @throws Exception
     */
    public static String getNodeText(Document doc, String path) throws Exception {
        
        Node node = getNode(doc, path);
        
        if (node == null) {
            return null;
        }
        
        Element element = (Element) node;
        return element.getTextTrim();
        
    }
    
    /**
     * 
     * @param node
     * @param attributeName
     * @return
     * @throws Exception
     */
    public static String getAttribute(Node node, String attributeName) throws Exception {
        Element element = (Element) node;
        return element.attributeValue(attributeName);
        
    }
    
    /**
     * 获取特定属性 属性值对应的节点值
     * 
     * @param attr
     * @param attrValue
     * @param document
     * @return
     */
    public static String getElementValueByAttr(Document document, String attr, String attrValue) {
        String retValue = "";
        String path = "/outputdatas/results/result/row/col[@" + attr + "='" + attrValue + "']";
        
        try {
            // linhz 2012-03-19
            retValue = getNodeText(document, path); // getNodesText(document,
                                                    // path, "##");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return retValue;
        
    }
    
    public static void main(String[] args) throws Exception {
        
        String xml = "123";
        xml = "<outputdatas><results><outputsubsinfo><subs_info><req_id>206461219053</req_id><req_code>GZ20120220001114299</req_code><subs_id>212066408211</subs_id><subs_code>200120220001333561</subs_code><stat>S0A</stat><acc_nbr/><prod_id>0</prod_id><prod_code/><prod_name/><action_id>3371</action_id><action_code>ADSLD_092</action_code><action_name>ADSL拨号变更(改体验速率)</action_name><serv_id>0</serv_id><serv_nbr/><cust_id>7407540</cust_id><cust_nbr>3020153323600000</cust_nbr><cust_name>测试7407540</cust_name><disc_id>0</disc_id><disc_name/><disc_code/><dialacct_id>0</dialacct_id><dialacct_name/><city_id>200</city_id><native_code>200</native_code></subs_info></outputsubsinfo></results><resultcode>0</resultcode><reason>OK</reason></outputdatas>";
        Document doc = null;
        
        try {
            doc = parseText(xml);
        } catch (Exception e) {
            System.out.println(e + "," + e.getCause());
            System.out.println(e.getCause().getCause());
        }
        if (doc == null) {
            return;
        }
        String str = null;
        String path = "/root/user";
        
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user/id";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user/name";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user[id='2']/name";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user[@uid='2']/name";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user[id='3']/name";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        path = "/root/user[@uid='3']/name";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        // path = "/root/user/id/";
        // org.dom4j.InvalidXPathException: Invalid XPath expression
        // str = getNodeText(doc,path);
        // System.out.println(path+"="+str);
        
        path = "/root/user/id2";
        str = getNodeText(doc, path);
        System.out.println(path + "=" + str);
        
        Node node = getNode(doc, "/root/user");
        str = getAttribute(node, "uid");
        System.out.println(str);
        
        str = getAttribute(node, "uid2");
        System.out.println(str);
        
    }
    
}
