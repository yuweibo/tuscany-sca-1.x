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

package org.apache.tuscany.sca.binding.ws.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.sca.assembly.xml.CompositeProcessor;
import org.apache.tuscany.sca.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.sca.binding.ws.DefaultWebServiceBindingFactory;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.DefaultContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.policy.DefaultPolicyFactory;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * Test reading/write WSDL interfaces.
 * 
 * @version $Rev$ $Date$
 */
public class WriteTestCase extends TestCase {

    XMLInputFactory inputFactory;
    DefaultStAXArtifactProcessorExtensionPoint staxProcessors;
    ExtensibleStAXArtifactProcessor staxProcessor;
    private AssemblyFactory factory;
    private PolicyFactory policyFactory;

    @Override
    public void setUp() throws Exception {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        factory = new DefaultAssemblyFactory();
        factories.addFactory(factory);
        policyFactory = new DefaultPolicyFactory();
        factories.addFactory(policyFactory);
        
        inputFactory = XMLInputFactory.newInstance();
        staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(extensionPoints);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, XMLInputFactory.newInstance(), XMLOutputFactory.newInstance());
        
        WebServiceBindingFactory wsFactory = new DefaultWebServiceBindingFactory();
        factories.addFactory(wsFactory);
        WSDLFactory wsdlFactory = new DefaultWSDLFactory();
        factories.addFactory(wsdlFactory);

        WebServiceBindingProcessor wsdlProcessor = new WebServiceBindingProcessor(factories);
        staxProcessors.addArtifactProcessor(wsdlProcessor);
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testReadWriteComponentType() throws Exception {
        InputStream is = getClass().getResourceAsStream("CalculatorImpl.componentType");
        ComponentType componentType = staxProcessor.read(is, ComponentType.class);
        assertNotNull(componentType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(componentType, bos);
    }

    public void testReadWriteComposite() throws Exception {
        InputStream is = getClass().getResourceAsStream("Calculator.composite");
        Composite composite = staxProcessor.read(is, Composite.class);
        assertNotNull(composite);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        staxProcessor.write(composite, bos);
    }

}
