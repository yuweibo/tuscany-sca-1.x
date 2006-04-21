/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.wire.jdk;

import org.apache.tuscany.core.wire.ProxyFactoryFactory;
import org.apache.tuscany.core.wire.TargetWireFactory;
import org.apache.tuscany.core.wire.SourceWireFactory;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Creates JDK Dynamic Proxy-based proxy factories
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class JDKProxyFactoryFactory implements ProxyFactoryFactory {

    public JDKProxyFactoryFactory() {
    }

    @Init(eager = true)
    public void init(){
    }

    public TargetWireFactory createTargetWireFactory() {
        return new JDKTargetWireFactory();
    }

    public SourceWireFactory createSourceWireFactory() {
        return new JDKSourceWireFactory();
    }

    public boolean isProxy(Object object) {
        if (object == null) {
            return false;
        } else {
            return Proxy.isProxyClass(object.getClass());
        }
    }

    public InvocationHandler getHandler(Object proxy) {
        if (proxy == null) {
            return null;
        } else {
            return Proxy.getInvocationHandler(proxy);
        }
    }

}
