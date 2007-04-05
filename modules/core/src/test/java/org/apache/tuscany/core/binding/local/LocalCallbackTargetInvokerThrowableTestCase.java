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
package org.apache.tuscany.core.binding.local;

import junit.framework.TestCase;

import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.idl.Operation;
import org.apache.tuscany.idl.impl.OperationImpl;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.Wire;

/**
 * @version $Rev$ $Date$
 */
public class LocalCallbackTargetInvokerThrowableTestCase extends TestCase {
    private LocalCallbackTargetInvoker invoker;

    /**
     * Verfies an exception thrown in the target is propagated to the client correctly
     */
    public void testThrowableTargetInvocation() throws Exception {
        Message message = new MessageImpl();
        message.setBody("foo");
        Message response = invoker.invoke(message);
        assertTrue(response.isFault());
        Object body = response.getBody();
        if (!(body instanceof InsidiousException)) {
            fail();
        }
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        Operation operation = new OperationImpl();
        operation.setName("echo");
        InvocationChain chain = new InvocationChainImpl(operation);
        chain.addInterceptor(new InsidiuousInterceptor());
        Wire wire = new WireImpl();
        wire.addCallbackInvocationChain(operation, chain);
        invoker = new LocalCallbackTargetInvoker(operation, wire);
    }

    private class InsidiousException extends RuntimeException {

    }

    private class InsidiuousInterceptor implements Interceptor {

        public Message invoke(Message msg) throws InsidiousException {
            throw new InsidiousException();
        }

        public void setNext(Interceptor next) {

        }

        public Interceptor getNext() {
            return null;
        }

        public boolean isOptimizable() {
            return false;
        }
    }


}
