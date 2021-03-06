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
package org.apache.tuscany.sca.databinding.jaxb.axiom;

import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.OMDataSourceExtBase;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;

/**
 *
 * @version $Rev$ $Date$
 */
public class JAXBDataSource extends OMDataSourceExtBase {
    private JAXBContext context;
    private Object element;

    public JAXBDataSource(Object element, JAXBContext context) {
        this.element = element;
        this.context = context;
    }

    private Marshaller getMarshaller() throws JAXBException {
        return JAXBContextHelper.getMarshaller(context);
    }

    private void releaseMarshaller(Marshaller marshaller) {
        JAXBContextHelper.releaseJAXBMarshaller(context, marshaller);
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        // FIXME: [rfeng] This is a quick and dirty implementation
        // We could use the fastinfoset to optimize the roundtrip
        StringWriter writer = new StringWriter();
        serialize(writer, new OMOutputFormat());
        StringReader reader = new StringReader(writer.toString());
        return StAXUtils.createXMLStreamReader(reader);
    }

    /**
     * If the writer is backed by an OutputStream, then return the OutputStream
     * @param writer
     * @return OutputStream or null
     */
    private static OutputStream getOutputStream(XMLStreamWriter writer) throws XMLStreamException {
        if (writer.getClass() == MTOMXMLStreamWriter.class) {
            return ((MTOMXMLStreamWriter)writer).getOutputStream();
        }

        try {
            Method method = writer.getClass().getMethod("getOutputStream");
            return (OutputStream)method.invoke(writer);

        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        try {
            // marshaller.setProperty(Marshaller.JAXB_ENCODING, format.getCharSetEncoding());
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    Marshaller marshaller = null;
                    try {
                        marshaller = getMarshaller();
                        // Marshalling directly to the output stream is faster than marshalling through the
                        // XMLStreamWriter. Take advantage of this optimization if there is an output stream.
                        OutputStream os = getOutputStream(xmlWriter);
                        if (os != null) {
                            marshaller.marshal(element, os);
                        } else {
                            marshaller.marshal(element, xmlWriter);
                        }
                    } finally {
                        releaseMarshaller(marshaller);
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new XMLStreamException(e.getException());
        }
    }

    public void serialize(final OutputStream output, OMOutputFormat format) throws XMLStreamException {
        try {
            // marshaller.setProperty(Marshaller.JAXB_ENCODING, format.getCharSetEncoding());
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    Marshaller marshaller = null;
                    try {
                        marshaller = getMarshaller();
                        marshaller.marshal(element, output);
                    } finally {
                        releaseMarshaller(marshaller);
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new XMLStreamException(e.getException());
        }
    }

    public void serialize(final Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                public Object run() throws Exception {
                    Marshaller marshaller = null;
                    try {
                        marshaller = getMarshaller();
                        marshaller.marshal(element, writer);
                    } finally {
                        releaseMarshaller(marshaller);
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw new XMLStreamException(e.getException());
        }
    }

    public Object getObject() {
        return element;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        return new JAXBDataSource(element, context);
    }

    public byte[] getXMLBytes(final String encoding) throws UnsupportedEncodingException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
                public byte[] run() throws JAXBException, XMLStreamException, UnsupportedEncodingException {
                    Marshaller marshaller = null;
                    try {
                        StringWriter sw = new StringWriter();
                        marshaller = getMarshaller();
                        marshaller.marshal(element, sw);
                        return sw.toString().getBytes(encoding);
                    } finally {
                        releaseMarshaller(marshaller);
                    }
                }
            });
        } catch (PrivilegedActionException e) {
            Throwable t = e.getCause();
            if (t instanceof UnsupportedEncodingException) {
                throw (UnsupportedEncodingException)t;
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public boolean isDestructiveWrite() {
        return false;
    }

}
