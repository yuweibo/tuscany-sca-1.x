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
package org.apache.tuscany.sca.implementation.resource.provider;

import org.apache.tuscany.sca.implementation.resource.ResourceImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * The model representing a resource implementation in an SCA assembly model.
 *
 * @version $Rev$ $Date$
 */
class ResourceImplementationProvider implements ImplementationProvider {
    
    private ResourceImplementation implementation;

    /**
     * Constructs a new resource implementation provider.
     */
    ResourceImplementationProvider(RuntimeComponent component, ResourceImplementation implementation) {
        this.implementation = implementation;
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        if ("get".equals(operation.getName())) {
            
            // Return an instance of our get resource invoker
            Invoker invoker = new GetResourceInvoker(implementation.getLocationURL().toString());
            return invoker;
            
        } else {
            
            // Return a dummy invoker that returns an "unsupported operation"
            // exception for now
            return new Invoker() {
                public Message invoke(Message msg) {
                    msg.setFaultBody(new UnsupportedOperationException());
                    return msg;
                }
            };
        }
    }
    
    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
    }

    public void stop() {
    }

}
