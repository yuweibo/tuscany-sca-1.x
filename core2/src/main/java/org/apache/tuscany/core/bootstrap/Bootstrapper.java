/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.bootstrap;

import org.apache.tuscany.core.implementation.Introspector;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.Loader;
import org.apache.tuscany.spi.loader.StAXPropertyFactory;
import org.apache.tuscany.spi.monitor.MonitorFactory;

/**
 * Interface that abstracts the process used to create a running Tuscany system.
 * Implementation of this may provide different mechanisms for creating the primoridal
 * system components used to boot the core to the level where it can support end-user applications.
 *
 * @version $Rev$ $Date$
 */
public interface Bootstrapper<T extends CompositeComponent> {
    /**
     * Return the MonitorFactory being used by the implementation to provide monitor
     * interfaces for the primordial components.
     *
     * @return the MonitorFactory being used by the bootstrapper
     */
    MonitorFactory getMonitorFactory();

    /**
     * Create the RuntimeComponent that forms the fundamental root of the component assembly.
     * This component has two children: a
     * {@link org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent} that is the root
     * for all system components, and a {@link org.apache.tuscany.spi.component.CompositeComponent} that is the root
     * for all application components.
     *
     * @return a new RuntimeComponent; basically a new Tuscany instance
     */
    RuntimeComponent<T> createRuntime();

    /**
     * Create a Deployer that can be used to deploy the system definition. This will most likely
     * only support a small subset of the available programming model.
     *
     * @return a new primordial Deployer
     */
    Deployer createDeployer();

    /**
     * Create a Loader for parsing a system definition represented as a XML SCDL file.
     *
     * @param propertyFactory the StAXPropertyFactory to be used to parse property values
     * @param introspector    the introspector to be used to extract component type information from a Java class
     * @return a new prmordial Loader
     */
    Loader createLoader(StAXPropertyFactory propertyFactory, Introspector introspector);

    /**
     * Create an Introspector for extracting component type information from a Java class.
     *
     * @return a new primordial Introspector
     */
    Introspector createIntrospector();

    /**
     * Create a Builder that can build runtime components from a model of the system definition.
     *
     * @param scopeRegistry a ScopeRegistry containing supported Scopes
     * @return a new primordial Builder
     */
    Builder createBuilder(ScopeRegistry scopeRegistry);

    /**
     * Create a ScopeRegistry that supports the Scopes supported for primordial components
     *
     * @param workContext the WorkContext the Scopes should use
     * @return a new primordial ScopeRegistry
     */
    ScopeRegistry createScopeRegistry(WorkContext workContext);

    /**
     * Create a Connector that can wire together primordial components.
     *
     * @return a new primordial Connector
     */
    Connector createConnector();
}
