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

package org.apache.tuscany.sca.binding.gdata.provider;

import org.apache.tuscany.sca.binding.gdata.GdataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.host.http.ServletHost;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.MessageFactory;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Implementation of the Atom binding provider.
 * 
 * @version $Rev$ $Date$
 */
class GdataServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeComponentService service;
    private GdataBinding binding;
    private ServletHost servletHost;
    private MessageFactory messageFactory;
    private String servletMapping;
    private Mediator mediator;

    GdataServiceBindingProvider(RuntimeComponent component,
                                RuntimeComponentService service,
                                GdataBinding binding,
                                ServletHost servletHost,
                                MessageFactory messageFactory,
                                Mediator mediator) {
        this.service = service;
        this.binding = binding;
        this.servletHost = servletHost;
        this.messageFactory = messageFactory;
        this.mediator = mediator;
    }

    public InterfaceContract getBindingInterfaceContract() {
        return service.getInterfaceContract();
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        RuntimeComponentService componentService = (RuntimeComponentService)service;
        RuntimeWire wire = componentService.getRuntimeWire(binding);

        GdataBindingListenerServlet servlet =
            new GdataBindingListenerServlet(wire, messageFactory, mediator, binding.getTitle());

        servletMapping = binding.getURI();
        if (!servletMapping.endsWith("/")) {
            servletMapping += "/";
        }
        if (!servletMapping.endsWith("*")) {
            servletMapping += "*";
        }
        servletHost.addServletMapping(servletMapping, servlet);

        // Save the actual binding URI in the binding
        binding.setURI(servletHost.getURLMapping(binding.getURI()).toString());
    }

    public void stop() {
        servletHost.removeServletMapping(servletMapping);
    }
}
