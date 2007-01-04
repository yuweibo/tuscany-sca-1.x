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
package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.implementation.java.ImplementationProcessorExtension;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.ProcessingException;
import org.apache.tuscany.spi.model.Scope;

/**
 * Processes the {@link Scope} annotation and updates the component type with the corresponding implmentation scope
 *
 * @version $Rev$ $Date$
 */
public class ScopeProcessor extends ImplementationProcessorExtension {

    public <T> void visitClass(CompositeComponent parent, Class<T> clazz,
                               PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                               DeploymentContext context)
        throws ProcessingException {
        org.osoa.sca.annotations.Scope annotation = clazz.getAnnotation(org.osoa.sca.annotations.Scope.class);
        if (annotation == null) {
            type.setImplementationScope(Scope.STATELESS);
            return;
        }
        String name = annotation.value();
        Scope scope;
        if ("COMPOSITE".equals(name)) {
            scope = Scope.COMPOSITE;
        } else if ("SESSION".equals(name)) {
            scope = Scope.SESSION;
        } else if ("CONVERSATION".equals(name)) {
            scope = Scope.CONVERSATION;
        } else if ("REQUEST".equals(name)) {
            scope = Scope.REQUEST;
        } else if ("SYSTEM".equals(name)) {
            scope = Scope.SYSTEM;
        } else {
            scope = new Scope(name);
        }
        type.setImplementationScope(scope);
    }
}
