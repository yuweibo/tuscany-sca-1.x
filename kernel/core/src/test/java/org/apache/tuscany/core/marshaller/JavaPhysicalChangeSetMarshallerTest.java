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
package org.apache.tuscany.core.marshaller;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import java.io.InputStream;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalReferenceDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.JavaPhysicalServiceDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalReferenceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalServiceDefinition;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;
import org.apache.tuscany.spi.model.physical.PhysicalComponentDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireDefinition;

/**
 * Test case for Java physical change set marshaller.
 * 
 * @version $Revision$ $Date$
 *
 */
public class JavaPhysicalChangeSetMarshallerTest extends TestCase {

    private ModelMarshallerRegistry registry;

    public JavaPhysicalChangeSetMarshallerTest(String arg0) {
        super(arg0);
    }

    public void setUp() {

        registry = new DefaultModelMarshallerRegistry();

        AbstractMarshallerExtension<?>[] marshallers = new AbstractMarshallerExtension<?>[6];

        marshallers[0] = new JavaPhysicalComponentDefinitionMarshaller();
        marshallers[1] = new JavaPhysicalServiceDefinitionMarshaller();
        marshallers[2] = new JavaPhysicalReferenceDefinitionMarshaller();
        marshallers[3] = new PhysicalOperationDefinitionMarshaller();
        marshallers[4] = new PhysicalWireDefinitionMarshaller();
        marshallers[5] = new PhysicalChangeSetMarshaller();

        for (int i = 0; i < 6; i++) {
            marshallers[i].setRegistry(registry);
        }

    }

    public void testUnmarshall() throws Exception {

        ClassLoader cl = getClass().getClassLoader();
        InputStream inputStream = cl.getResourceAsStream("marshall/javaChangeSet.xml");
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

        while(reader.next() != START_ELEMENT) {            
        }
        PhysicalChangeSet changeSet = (PhysicalChangeSet)registry.unmarshall(reader);
        assertNotNull(changeSet);
        Set<? extends PhysicalComponentDefinition> pcds = changeSet.getComponentDefinitions();
        assertEquals(2, pcds.size());
        for (PhysicalComponentDefinition pcd : pcds) {

            assertTrue(pcd instanceof JavaPhysicalComponentDefinition);
            String componentId = pcd.getComponentId().toString();
            assertTrue("cmp1".equals(componentId) || "cmp2".equals(componentId));

            JavaPhysicalComponentDefinition jpcd = (JavaPhysicalComponentDefinition)pcd;

            Set<JavaPhysicalReferenceDefinition> refs = jpcd.getReferences();
            assertEquals(1, refs.size());
            JavaPhysicalReferenceDefinition ref = refs.iterator().next();
            if ("cmp1".equals(componentId)) {
                assertEquals("rf1", ref.getName());
                Set<PhysicalOperationDefinition> pods = ref.getOperations();
                assertEquals(1, pods.size());
                PhysicalOperationDefinition pod = pods.iterator().next();
                assertEquals("op2", pod.getName());
            } else {
                assertEquals("rf2", ref.getName());
                Set<PhysicalOperationDefinition> pods = ref.getOperations();
                assertEquals(1, pods.size());
                PhysicalOperationDefinition pod = pods.iterator().next();
                assertEquals("op1", pod.getName());
            }

            Set<JavaPhysicalServiceDefinition> svs = jpcd.getServices();
            assertEquals(1, svs.size());
            JavaPhysicalServiceDefinition sv = svs.iterator().next();
            if ("cmp1".equals(componentId)) {
                assertEquals("sv1", sv.getName());
                Set<PhysicalOperationDefinition> pods = sv.getOperations();
                assertEquals(1, pods.size());
                PhysicalOperationDefinition pod = pods.iterator().next();
                assertEquals("op1", pod.getName());
            } else {
                assertEquals("sv2", sv.getName());
                Set<PhysicalOperationDefinition> pods = sv.getOperations();
                assertEquals(1, pods.size());
                PhysicalOperationDefinition pod = pods.iterator().next();
                assertEquals("op2", pod.getName());
            }

        }

        Set<PhysicalWireDefinition> pwds = changeSet.getWireDefinitions();

        assertEquals(2, changeSet.getWireDefinitions().size());
        for (PhysicalWireDefinition pwd : pwds) {

            String sourceUri = pwd.getSourceUri().toString();
            String targetUri = pwd.getTargetUri().toString();

            assertTrue(("cmp1#rf1".equals(sourceUri) && "cmp2#sv2".equals(targetUri)) 
                       || ("cmp2#rf2".equals(sourceUri) && "cmp1#sv1"
                .equals(targetUri)));

            Set<PhysicalOperationDefinition> pods = pwd.getOperations();
            assertEquals(1, pods.size());
            PhysicalOperationDefinition pod = pods.iterator().next();

            if (sourceUri.equals("cmp1#rf1")) {
                assertEquals("op2", pod.getName());
            } else {
                assertEquals("op1", pod.getName());
            }
        }

    }

}
