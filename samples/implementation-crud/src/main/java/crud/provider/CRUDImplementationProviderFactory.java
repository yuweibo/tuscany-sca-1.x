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
package crud.provider;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospector;
import org.apache.tuscany.provider.ImplementationProvider;
import org.apache.tuscany.provider.ImplementationProviderFactory;

import crud.impl.CRUDImplementationImpl;

/**
 * The model representing a sample CRUD implementation in an SCA assembly model.
 * 
 * @version $$Rev$$ $$Date: 2007-04-23 19:18:54 -0700 (Mon, 23 Apr
 *          2007) $$
 */
public class CRUDImplementationProviderFactory extends CRUDImplementationImpl implements ImplementationProviderFactory {

    /**
     * Constructs a new CRUD implementation.
     */
    public CRUDImplementationProviderFactory(
                               AssemblyFactory assemblyFactory,
                              JavaInterfaceFactory javaFactory,
                              JavaInterfaceIntrospector introspector) {
        super(assemblyFactory, javaFactory, introspector);
    }

    public ImplementationProvider createImplementationProvider(RuntimeComponent component) {
        return new CRUDImplementationProvider(component, this);
    }
}
