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

package org.apache.tuscany.sca.implementation.bpel;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * @version $Rev$ $Date$
 */
public class BPELImplementationProcessorTestCase extends TestCase {
    
    protected static final QName IMPLEMENTATION_BPEL = new QName(Constants.SCA10_NS, "implementation.bpel");

    private static final String COMPOSITE =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
        + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:hns=\"http://tuscany.apache.org/implementation/bpel/example/helloworld\" targetNamespace=\"http://bpel\" name=\"bpel\">"
            + " <component name=\"BPELHelloWorldComponent\">"
            + "   <implementation.bpel process=\"hns:HelloWorld\" />"
            + "</component>";

    private static final String COMPOSITE_INVALID =
        "<?xml version=\"1.0\" encoding=\"ASCII\"?>" 
            + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:hns=\"http://tuscany.apache.org/implementation/bpel/example/helloworld\" targetNamespace=\"http://bpel\" name=\"bpel\">"
            + " <component name=\"BPELHelloWorldComponent\">"
            + "   <implementation.bpel/>"
            + "</component>";

    private StAXArtifactProcessorExtensionPoint staxProcessors;
    private XMLInputFactory xmlFactory;

    @Override
    protected void setUp() throws Exception {
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlFactory = XMLInputFactory.newInstance();
    }

    /**
     * Test parsing valid composite definition. Valid composite populated with correct values expected.
     * @throws Exception
     */
    public void testLoadValidComposite() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(COMPOSITE));
        
        StAXArtifactProcessor<BPELImplementation> bpelProcessor = staxProcessors.getProcessor(BPELImplementation.class);
        
        while(true) {
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT && IMPLEMENTATION_BPEL.equals(reader.getName())) {
                break;
            }
        }

        BPELImplementation implementation = bpelProcessor.read(reader);
        
        assertNotNull(implementation);
        assertEquals(new QName("http://tuscany.apache.org/implementation/bpel/example/helloworld", "HelloWorld"), implementation.getProcess());
    }

    /**
     * Test parsing invalid composite definition. Exception should be thrown
     * @throws Exception
     */
    public void testLoadInvalidComposite() throws Exception {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(new StringReader(COMPOSITE_INVALID));

        StAXArtifactProcessor<BPELImplementation> bpelProcessor = staxProcessors.getProcessor(BPELImplementation.class);
        
        while(true) {
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT && IMPLEMENTATION_BPEL.equals(reader.getName())) {
                break;
            }
        }

        try {
            bpelProcessor.read(reader);
            
            fail("InvalidException should have been thrown");
        } catch(Exception e) {
            assertTrue(true);
        }
    }    
}
