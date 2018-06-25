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

package org.apache.tuscany.sca.binding.jsonrpc.provider;

import org.apache.tuscany.sca.databinding.json.client.KyroClient;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invoker for the JSONRPC Binding
 *
 * @version $Rev$ $Date$
 */
public class JSONRPCBindingInvoker implements Invoker {
    Operation operation;
    String uri;
    private Method remoteMethod;
    private Object proxy;
    private Class<?> service;

    public JSONRPCBindingInvoker(Operation operation, String uri, Class<?> service, Method remoteMethod) {
        this.operation = operation;
        this.uri = uri;
        this.service = service;
        this.remoteMethod = remoteMethod;
    }

    public Message invoke(Message msg) {
        try {
            Object[] args = msg.getBody();
            Object resp = invokeTarget(args);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setFaultBody(e.getCause());
        } catch (Throwable e) {
            msg.setFaultBody(e);
        }
        return msg;
    }

    private Object invokeTarget(final Object payload) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (null == proxy) {
            proxy = KyroClient.lookup(service, this.uri);
        }
        remoteMethod = proxy.getClass().getMethod(remoteMethod.getName(), remoteMethod.getParameterTypes());
        if (payload != null && !payload.getClass().isArray()) {
            return remoteMethod.invoke(proxy, payload);
        } else {
            return remoteMethod.invoke(proxy, (Object[]) payload);
        }
    }

}
