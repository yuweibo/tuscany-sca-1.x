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
package org.apache.tuscany.binding.jsonrpc;

import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

public class JSONRPCBindingBuilderTestCase extends TestCase {

    public void testSetServletHost() {
        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
        ServletHost mockServletHost = createMock(ServletHost.class);
        replay(mockServletHost);
        bindingBuilder.setServletHost(mockServletHost);
        assertEquals(mockServletHost, bindingBuilder.getServletHost());
    }

    public void testGetBindingType() {
        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
        assertEquals(JSONRPCBinding.class, bindingBuilder.getBindingType());
    }

    @SuppressWarnings("unchecked")
    public void testBuildCompositeComponentBoundServiceDefinitionOfJSONRPCBindingDeploymentContext() {
        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        BoundServiceDefinition<JSONRPCBinding> mockServiceDefinition = createMock((new BoundServiceDefinition<JSONRPCBinding>()).getClass());
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        try {
            ServiceContract<?> contract = registry.introspect(JSONRPCService.class);
        
            expect(mockServiceDefinition.getServiceContract()).andStubReturn(contract);
            expect(mockServiceDefinition.getName()).andReturn("test_service");
            replay(mockServiceDefinition);
            DeploymentContext mockDeploymentContext = createMock(DeploymentContext.class);
            replay(mockDeploymentContext);
            
            JSONRPCService jsonService = (JSONRPCService)bindingBuilder.build(mockParent, mockServiceDefinition, mockDeploymentContext);
            assertEquals(JSONRPCService.class, jsonService.getInterface());
        
        } catch (InvalidServiceContractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(e.toString());
        }
    }

}
