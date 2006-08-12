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
package org.apache.tuscany.container.groovy;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $Rev$ $Date$
 */
public class GroovyComponentTypeLoader extends ComponentTypeLoaderExtension<GroovyImplementation> {
    protected Class<GroovyImplementation> getImplementationClass() {
        return GroovyImplementation.class;
    }

    public void load(CompositeComponent parent, GroovyImplementation implementation, DeploymentContext context)
        throws LoaderException {
        GroovyComponentType componentType = new GroovyComponentType();
        // for now, default to module
        componentType.setLifecycleScope(Scope.MODULE);
        implementation.setComponentType(componentType);
    }

}
