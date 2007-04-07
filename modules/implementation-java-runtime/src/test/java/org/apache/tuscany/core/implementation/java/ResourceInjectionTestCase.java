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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;

import org.apache.tuscany.api.annotation.Resource;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.ResourceObjectFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ResourceInjectionTestCase extends TestCase {

    public void testResourceMemberInjection() throws Exception {
        ScopeContainer containter = EasyMock.createNiceMock(ScopeContainer.class);
        Constructor<Foo> ctor = Foo.class.getConstructor();
        Field field = Foo.class.getDeclaredField("resource");
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName(new URI("component"));
        configuration.setGroupId(URI.create("composite"));
        configuration.setInstanceFactory(new PojoObjectFactory<Foo>(ctor));
        configuration.addResourceSite("bar", field);
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(containter);

        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getTargetInstance()).andReturn("result");
        EasyMock.replay(wire);

        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("result");
        EasyMock.replay(host);

        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, host);
        component.addResourceFactory("bar", factory);

        Foo foo = (Foo) component.createInstance();
        assertEquals("result", foo.resource);
    }


    public void testResourceConstructorInjection() throws Exception {
        ScopeContainer containter = EasyMock.createNiceMock(ScopeContainer.class);
        Constructor<FooConstructor> ctor = FooConstructor.class.getConstructor(String.class);
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setName(new URI("component"));
        configuration.setGroupId(URI.create("composite"));
        configuration.setInstanceFactory(new PojoObjectFactory<FooConstructor>(ctor));
        ConstructorDefinition<?> definition = new ConstructorDefinition(ctor);
        definition.getParameters()[0].setName("bar");
        definition.getParameters()[0].setClassifer(Resource.class);
        configuration.setConstructor(definition);
        JavaAtomicComponent component = new JavaAtomicComponent(configuration);
        component.setScopeContainer(containter);

        Wire wire = EasyMock.createMock(Wire.class);
        EasyMock.expect(wire.getTargetInstance()).andReturn("result");
        EasyMock.replay(wire);
        ResourceHost host = EasyMock.createMock(ResourceHost.class);
        EasyMock.expect(host.resolveResource(EasyMock.eq(String.class))).andReturn("result");
        EasyMock.replay(host);

        ResourceObjectFactory<String> factory = new ResourceObjectFactory<String>(String.class, false, host);
        component.addResourceFactory("bar", factory);

        FooConstructor foo = (FooConstructor) component.createInstance();
        assertEquals("result", foo.resource);
    }

    public static class Foo {
        protected String resource;

        public Foo() {
        }

    }

    public static class FooConstructor {
        protected String resource;

        public FooConstructor(String resource) {
            this.resource = resource;
        }

    }
}
