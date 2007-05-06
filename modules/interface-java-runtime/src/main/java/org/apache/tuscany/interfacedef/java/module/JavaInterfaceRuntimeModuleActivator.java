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

package org.apache.tuscany.interfacedef.java.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.core.ModuleActivator;
import org.apache.tuscany.interfacedef.java.DefaultJavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.ExtensibleJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.interfacedef.java.xml.JavaInterfaceProcessor;

/**
 * @version $Rev$ $Date$
 */
public class JavaInterfaceRuntimeModuleActivator implements ModuleActivator {
    
    private JavaInterfaceFactory javaFactory;
    private JavaInterfaceIntrospectorExtensionPoint visitors;
    
    public JavaInterfaceRuntimeModuleActivator() {
        javaFactory = new DefaultJavaInterfaceFactory();
        visitors = new DefaultJavaInterfaceIntrospectorExtensionPoint();
    }

    public Map<Class, Object> getExtensionPoints() {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(JavaInterfaceIntrospectorExtensionPoint.class, visitors);
        return map;
    }

    public void start(ExtensionPointRegistry registry) {
        
        // Register <interface.java> processor
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        JavaInterfaceIntrospector introspector = new ExtensibleJavaInterfaceIntrospector(javaFactory, visitors);
        JavaInterfaceProcessor javaInterfaceProcessor = new JavaInterfaceProcessor(javaFactory, introspector);
        processors.addArtifactProcessor(javaInterfaceProcessor);
        
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
