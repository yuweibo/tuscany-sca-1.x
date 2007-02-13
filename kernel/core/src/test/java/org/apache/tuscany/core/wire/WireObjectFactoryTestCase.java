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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireObjectFactoryTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testCreateInstance() throws Exception {
        Operation<Type> op = new Operation<Type>("hello", null, null, null);
        OutboundInvocationChain chain = new OutboundInvocationChainImpl(op);
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
        chains.put(op, chain);
        EasyMock.expect(wire.getOutboundInvocationChains()).andReturn(chains);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        WireService service = EasyMock.createMock(WireService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    @SuppressWarnings("unchecked")
    public void testOptimizedCreateInstance() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract(Foo.class);
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getOutboundInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.expect(wire.getTargetService()).andReturn(new Foo() {
            public void hello() {
            }
        });
        EasyMock.replay(wire);
        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, null);
        factory.getInstance();
        EasyMock.verify(wire);

    }

    /**
     * Verifies that a proxy is created when the required client contract is different than the wire contract
     */
    @SuppressWarnings("unchecked")
    public void testCannotOptimizeDifferentContractsCreateInstance() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract(Object.class);
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getOutboundInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.replay(wire);
        WireService service = EasyMock.createMock(WireService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    @SuppressWarnings("unchecked")
    public void testNoJavaInterfaceCreateInstance() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract();
        OutboundWire wire = EasyMock.createMock(OutboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.expect(wire.getOutboundInvocationChains()).andReturn((Map) Collections.emptyMap());
        EasyMock.replay(wire);
        WireService service = EasyMock.createMock(WireService.class);
        service.createProxy(EasyMock.eq(Foo.class), EasyMock.eq(wire), EasyMock.isA(Map.class));
        EasyMock.expectLastCall().andReturn(new Foo() {
            public void hello() {

            }
        });
        EasyMock.replay(service);

        WireObjectFactory<Foo> factory = new WireObjectFactory<Foo>(Foo.class, wire, service);
        factory.getInstance();
        EasyMock.verify(service);
        EasyMock.verify(wire);
    }

    private interface Foo {
        void hello();
    }


}
