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
package org.apache.tuscany.sca.contribution.jee;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;

/**
 * Compute componentType with EJB3 business interfaces translating into SCA services.
 * 
 * @version $Rev$ $Date$
 */
public interface JavaEEExtension {
    // The EJBImplementation model is a straight implementation so we deal in 
    // component types
    ComponentType createImplementationEjbComponentType(EjbModuleInfo ejbModule, String ejbName);
    
    // The JEEImplemenation model is a composite and so we have to fluff
    // up the composite contents to match the JEE artifact
    void createImplementationJeeComposite(EjbModuleInfo ejbModule, Composite composite);
    void createImplementationJeeComposite(JavaEEApplicationInfo appInfo, Composite composite);
}
