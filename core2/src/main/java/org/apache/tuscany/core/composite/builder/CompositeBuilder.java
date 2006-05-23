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
package org.apache.tuscany.core.composite.builder;

import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.core.context.CompositeContextImpl;

/**
 * @version $Rev$ $Date$
 */
public class CompositeBuilder implements ComponentBuilder<CompositeImplementation> {
    public ComponentContext build(CompositeContext parent, Component<CompositeImplementation> component) throws BuilderConfigException {
        CompositeContextImpl<?> context = new CompositeContextImpl(component.getName(), parent, null);
        return context;
    }
}
