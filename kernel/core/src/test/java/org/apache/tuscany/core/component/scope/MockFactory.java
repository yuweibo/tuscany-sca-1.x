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
package org.apache.tuscany.core.component.scope;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.wire.Wire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.component.ReflectiveInstanceFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public final class MockFactory {

    private MockFactory() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, AtomicComponent> createWiredComponents(String source,
                                                                     Class<?> sourceClass,
                                                                     ScopeContainer sourceScopeContainer,
                                                                     String target,
                                                                     Class<?> targetClass,
                                                                     ScopeContainer targetScopeContainer)
        throws NoSuchMethodException, URISyntaxException {

        Map<String, AtomicComponent> components = new HashMap<String, AtomicComponent>();
        AtomicComponent targetComponent = createAtomicComponent(target, targetScopeContainer, targetClass);
        PojoConfiguration sourceConfig = new PojoConfiguration();
        Constructor<?> ctr = sourceClass.getConstructor();
        Method[] sourceMethods = sourceClass.getMethods();
        Class[] interfaces = targetClass.getInterfaces();
        EagerInit eager = targetClass.getAnnotation(EagerInit.class);
        if (eager != null) {
            sourceConfig.setInitLevel(50);
        }
        Method setter = null;
        EventInvoker initInvoker = null;
        EventInvoker destroyInvoker = null;
        for (Class interfaze : interfaces) {
            for (Method method : sourceMethods) {
                if (method.getParameterTypes().length == 1) {
                    if (interfaze.isAssignableFrom(method.getParameterTypes()[0])) {
                        setter = method;
                    }
                }
                if (method.getAnnotation(Init.class) != null) {
                    MethodEventInvoker<Object> invoker = new MethodEventInvoker<Object>(method);
                    sourceConfig.setInitInvoker(invoker);
                    initInvoker = invoker;

                } else if (method.getAnnotation(Destroy.class) != null) {
                    MethodEventInvoker<Object> invoker = new MethodEventInvoker<Object>(method);
                    sourceConfig.setDestroyInvoker(invoker);
                    destroyInvoker = invoker;
                }
            }
        }
        if (setter == null) {
            throw new IllegalArgumentException("No setter found on source for target");
        }
        sourceConfig.setInstanceFactory(new PojoObjectFactory(ctr));
        sourceConfig.setInstanceFactory2(new ReflectiveInstanceFactory(ctr, null, null, initInvoker, destroyInvoker));
        sourceConfig.addReferenceSite(setter.getName(), setter);
        sourceConfig.setName(new URI(source));
        AtomicComponent sourceComponent = new SystemAtomicComponentImpl(sourceConfig);
        sourceComponent.setScopeContainer(sourceScopeContainer);
        Wire wire = new WireImpl();
        wire.setSourceUri(URI.create("#" + setter.getName()));
        wire.setSourceContract(new JavaServiceContract(targetClass));
        wire.setTarget(targetComponent);
        sourceComponent.attachWire(wire);
        components.put(source, sourceComponent);
        components.put(target, targetComponent);
        return components;
    }

    @SuppressWarnings("unchecked")
    public static AtomicComponent createAtomicComponent(String name, ScopeContainer container, Class<?> clazz)
        throws NoSuchMethodException, URISyntaxException {
        PojoConfiguration configuration = new PojoConfiguration();
        EagerInit eager = clazz.getAnnotation(EagerInit.class);
        if (eager != null) {
            configuration.setInitLevel(50);
        }
        Method[] methods = clazz.getMethods();
        EventInvoker initInvoker = null;
        EventInvoker destroyInvoker = null;
        for (Method method : methods) {
            if (method.getAnnotation(Init.class) != null) {
                MethodEventInvoker<Object> invoker = new MethodEventInvoker<Object>(method);
                configuration.setInitInvoker(invoker);
                initInvoker = invoker;
            } else if (method.getAnnotation(Destroy.class) != null) {
                MethodEventInvoker<Object> invoker = new MethodEventInvoker<Object>(method);
                configuration.setDestroyInvoker(invoker);
                destroyInvoker = invoker;
            }
        }
        configuration.setName(new URI(name));
        Constructor<?> ctr = clazz.getConstructor();
        configuration.setInstanceFactory(new PojoObjectFactory(ctr));
        configuration.setInstanceFactory2(new ReflectiveInstanceFactory(ctr, null, null, initInvoker, destroyInvoker));
        AtomicComponent component = new SystemAtomicComponentImpl(configuration);
        component.setScopeContainer(container);
        return component;
    }

}
