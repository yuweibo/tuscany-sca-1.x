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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * The default implementation of a {@link org.apache.tuscany.spi.component.Reference}
 *
 * @version $Rev$ $Date$
 */
public class ReferenceImpl extends AbstractSCAObject implements Reference {
    private ServiceContract<?> serviceContract;
    private List<ReferenceBinding> bindings = new ArrayList<ReferenceBinding>();

    public ReferenceImpl(URI name, ServiceContract<?> contract) {
        super(name);
        this.serviceContract = contract;
    }

    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

    public List<ReferenceBinding> getReferenceBindings() {
        return Collections.unmodifiableList(bindings);
    }

    public void addReferenceBinding(ReferenceBinding binding) {
        binding.setReference(this);
        bindings.add(binding);
    }

    public void start() {
        super.start();
        for (ReferenceBinding binding : bindings) {
            binding.start();
        }
    }

    public void stop() {
        super.stop();
        for (ReferenceBinding binding : bindings) {
            binding.stop();
        }
    }

}
