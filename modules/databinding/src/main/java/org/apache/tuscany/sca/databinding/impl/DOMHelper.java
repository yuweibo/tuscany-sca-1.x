/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.databinding.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper for DOM
 */
public final class DOMHelper {
    private static DocumentBuilderFactory FACTORY;

    private DOMHelper() {
    }

    public static Document newDocument() throws ParserConfigurationException {
        return newDocumentBuilder().newDocument();
    }

    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        init();
        return FACTORY.newDocumentBuilder();
    }

    /**
     * 
     */
    private synchronized static void init() {
        if (FACTORY == null) {
            FACTORY = DocumentBuilderFactory.newInstance();
            FACTORY.setNamespaceAware(true);
        }
    }

    public static QName getQName(Node node) {
        String ns = node.getNamespaceURI();
        if (ns == null) {
            ns = "";
        }
        // node.getLocalName() will return null if it is created using DOM Level
        // 1 method
        // such as createElement()
        return new QName(ns, node.getNodeName());
    }

    public static Element createElement(Document document, QName name) {
        String prefix = name.getPrefix();
        String qname =
            (prefix != null && prefix.length() > 0) ? prefix + ":" + name.getLocalPart() : name.getLocalPart();
        return document.createElementNS(name.getNamespaceURI(), qname);
    }

    /**
     * Wrap an element as a DOM document
     * @param node
     * @return
     */
    public static Document promote(Node node) {
        if (node instanceof Document) {
            return (Document)node;
        }
        Element element = (Element)node;
        Document doc = element.getOwnerDocument();
        if (doc.getDocumentElement() == element) {
            return doc;
        }
        doc = (Document)element.getOwnerDocument().cloneNode(false);
        Element schema = (Element)doc.importNode(element, true);
        doc.appendChild(schema);
        Node parent = element.getParentNode();
        while (parent instanceof Element) {
            Element root = (Element)parent;
            NamedNodeMap nodeMap = root.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Attr attr = (Attr)nodeMap.item(i);
                String name = attr.getName();
                if ("xmlns".equals(name) || name.startsWith("xmlns:")) {
                    if (schema.getAttributeNode(name) == null) {
                        schema.setAttributeNodeNS((Attr)doc.importNode(attr, true));
                    }
                }
            }
            parent = parent.getParentNode();
        }
        return doc;
    }

    /**
     * @param context
     * @param element
     */
    public static Element adjustElementName(TransformationContext context, Element element) {
        if (context != null) {
            DataType dataType = context.getTargetDataType();
            Object logical = dataType == null ? null : dataType.getLogical();
            if (!(logical instanceof XMLType)) {
                return element;
            }
            XMLType xmlType = (XMLType)logical;
            QName name = new QName(element.getNamespaceURI(), element.getLocalName());
            if (xmlType.isElement() && !xmlType.getElementName().equals(name)) {
                QName newName = xmlType.getElementName();
                String prefix = element.getPrefix();
                String qname = newName.getLocalPart();
                if (prefix != null && !prefix.equals("")) {
                    qname = prefix + ":" + qname;
                }
                Element newElement = element.getOwnerDocument().createElementNS(newName.getNamespaceURI(), qname);
                // newElement.setPrefix(prefix);
                NodeList nodeList = element.getChildNodes();
                // Need to copy the nodes from the list fisrt as the appendChild() will change the list
                int length = nodeList.getLength();
                List<Node> nodes = new ArrayList<Node>();
                for (int i = 0; i < length; i++) {
                    nodes.add(nodeList.item(i));
                }
                NamedNodeMap attrs = element.getAttributes();
                for (int i = 0; i < attrs.getLength(); i++) {
                    nodes.add(attrs.item(i));
                }
                for (int i = 0; i < nodes.size(); i++) {
                    Node node = nodes.get(i);
                    if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                        newElement.setAttributeNodeNS((Attr)node.cloneNode(true));
                    } else {
                        newElement.appendChild(nodes.get(i));
                    }
                }
                return newElement;
            }
        }
        return element;
    }

}
