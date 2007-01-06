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
package org.apache.tuscany.core.builder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import static org.apache.tuscany.spi.model.Multiplicity.ONE_ONE;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class BuilderRegistryTestCase extends TestCase {
    private DeploymentContext deploymentContext;
    //private BuilderRegistryImpl registry;
    private CompositeComponent parent;

    public void testRegistration() throws Exception {
        MockBuilder builder = new MockBuilder();
        BuilderRegistry registry = new BuilderRegistryImpl(null, null, null);
        registry.register(CompositeImplementation.class, builder);
        CompositeImplementation implementation = new CompositeImplementation();
        ComponentDefinition<CompositeImplementation> componentDefinition =
            new ComponentDefinition<CompositeImplementation>(implementation);
        componentDefinition.getImplementation().setComponentType(new CompositeComponentType());
        registry.build(parent, componentDefinition, deploymentContext);
    }

    @SuppressWarnings({"unchecked"})
    public void testServiceBindingBuilderDispatch() throws Exception {
        WireService wireService = EasyMock.createMock(WireService.class);
        wireService.createWires(EasyMock.isA(ServiceBinding.class),
            (ServiceContract) EasyMock.isNull(), EasyMock.isA(String.class)
        );
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(wireService);
        BuilderRegistry registry = new BuilderRegistryImpl(null, wireService, null);
        ServiceBinding binding = EasyMock.createNiceMock(ServiceBinding.class);
        EasyMock.replay(binding);
        BindingBuilder<MockBindingDefinition> builder = EasyMock.createMock(BindingBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(CompositeComponent.class),
            EasyMock.isA(BoundServiceDefinition.class),
            EasyMock.isA(MockBindingDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(binding).times(2);
        EasyMock.replay(builder);
        registry.register(MockBindingDefinition.class, builder);
        List<BindingDefinition> bindingDefs = new ArrayList<BindingDefinition>();
        bindingDefs.add(new MockBindingDefinition());
        bindingDefs.add(new MockBindingDefinition());
        BoundServiceDefinition definition = new BoundServiceDefinition("foo", null, bindingDefs, false, new URI("foo"));
        Service service = registry.build(parent, definition, deploymentContext);
        assertEquals(2, service.getServiceBindings().size());
        EasyMock.verify(wireService);
    }

    @SuppressWarnings({"unchecked"})
    public void testReferenceBindingBuilderDispatch() throws Exception {
        WireService wireService = EasyMock.createMock(WireService.class);
        wireService.createWires(EasyMock.isA(ReferenceBinding.class),
            (ServiceContract) EasyMock.isNull(), (QualifiedName) EasyMock.isNull()
        );
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(wireService);
        BuilderRegistry registry = new BuilderRegistryImpl(null, wireService, null);
        ReferenceBinding binding = EasyMock.createNiceMock(ReferenceBinding.class);
        EasyMock.replay(binding);
        BindingBuilder<MockBindingDefinition> builder = EasyMock.createMock(BindingBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(CompositeComponent.class),
            EasyMock.isA(BoundReferenceDefinition.class),
            EasyMock.isA(MockBindingDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(binding).times(2);
        EasyMock.replay(builder);
        registry.register(MockBindingDefinition.class, builder);
        List<BindingDefinition> bindingDefs = new ArrayList<BindingDefinition>();
        bindingDefs.add(new MockBindingDefinition());
        bindingDefs.add(new MockBindingDefinition());
        BoundReferenceDefinition definition = new BoundReferenceDefinition("foo", null, bindingDefs, ONE_ONE);
        Reference reference = registry.build(parent, definition, deploymentContext);
        assertEquals(2, reference.getReferenceBindings().size());
        EasyMock.verify(wireService);
    }

    @SuppressWarnings({"unchecked"})
    public void testComponentImplementationDispatch() throws Exception {
        WireService wireService = EasyMock.createMock(WireService.class);
        wireService.createWires(EasyMock.isA(AtomicComponent.class),
            EasyMock.isA(ComponentDefinition.class));
        EasyMock.replay(wireService);
        BuilderRegistry registry = new BuilderRegistryImpl(null, wireService, null);

        AtomicComponent component = EasyMock.createNiceMock(AtomicComponent.class);
        EasyMock.replay(component);
        ComponentBuilder<FooImplementation> builder = EasyMock.createMock(ComponentBuilder.class);
        EasyMock.expect(builder.build(EasyMock.isA(CompositeComponent.class),
            EasyMock.isA(ComponentDefinition.class),
            EasyMock.isA(DeploymentContext.class))).andReturn(component);
        EasyMock.replay(builder);
        registry.register(FooImplementation.class, builder);

        FooImplementation impl = new FooImplementation();
        impl.setComponentType(new ComponentType());
        ComponentDefinition<FooImplementation> definition = new ComponentDefinition<FooImplementation>("foo", impl);
        Component ret = registry.build(parent, definition, deploymentContext);
        assertNotNull(ret);
        EasyMock.verify(wireService);
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new RootDeploymentContext(null, null, null, null);
        parent = EasyMock.createNiceMock(CompositeComponent.class);
        EasyMock.replay(parent);
    }

    private class MockBuilder implements ComponentBuilder<CompositeImplementation> {
        public Component build(CompositeComponent parent,
                               ComponentDefinition componentDefinition,
                               DeploymentContext deploymentContext) throws BuilderConfigException {
            return null;
        }
    }

    private class MockBindingDefinition extends BindingDefinition {

    }

    private class FooImplementation extends Implementation<ComponentType> {

    }


}
