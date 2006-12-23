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
package org.apache.tuscany.core.implementation.system.loader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.processor.ConstructorProcessor;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.HeuristicPojoProcessor;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorService;
import org.apache.tuscany.core.implementation.processor.ImplementationProcessorServiceImpl;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.implementation.processor.ServiceProcessor;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.mock.component.BasicInterfaceImpl;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentTypeLoaderTestCase extends TestCase {
    private SystemComponentTypeLoader loader;

    public void testIntrospectUnannotatedClass() throws ProcessingException {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        SystemImplementation impl = new SystemImplementation(BasicInterfaceImpl.class);
        PojoComponentType<?, ?, ?> componentType = loader.loadByIntrospection(parent, impl, null);
        ServiceDefinition service = componentType.getServices().get(BasicInterface.class.getName());
        assertEquals(BasicInterface.class, service.getServiceContract().getInterfaceClass());
        Property<?> property = componentType.getProperties().get("publicProperty");
        assertEquals(String.class, property.getJavaType());
        ReferenceDefinition referenceDefinition = componentType.getReferences().get("protectedReference");
        assertEquals(BasicInterface.class, referenceDefinition.getServiceContract().getInterfaceClass());
    }

    protected void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistryImpl interfaceProcessorRegistry = new JavaInterfaceProcessorRegistryImpl();
        ImplementationProcessorService service =
            new ImplementationProcessorServiceImpl(interfaceProcessorRegistry);
        IntrospectionRegistryImpl registry = new IntrospectionRegistryImpl();
        registry.setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.Monitor.class));
        registry.registerProcessor(new ConstructorProcessor(service));
        registry.registerProcessor(new DestroyProcessor());
        registry.registerProcessor(new InitProcessor());
        registry.registerProcessor(new ScopeProcessor());
        registry.registerProcessor(new PropertyProcessor(service));
        registry.registerProcessor(new ReferenceProcessor(interfaceProcessorRegistry));
        registry.registerProcessor(new ServiceProcessor(service));
        registry.registerProcessor(new HeuristicPojoProcessor(service));
        loader = new SystemComponentTypeLoader(registry);
    }
}
