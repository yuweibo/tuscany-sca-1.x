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
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;

/**
 * Verifies retrieval of standard scope contexts from the default scope registry
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScopeRegistryTestCase extends TestCase {
    public void testScopeContextCreation() throws Exception {
        WorkContext context = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        scopeRegistry.registerFactory(Scope.REQUEST, new RequestScopeObjectFactory(context, null));
        HttpSessionScopeObjectFactory sessionFactory = new HttpSessionScopeObjectFactory(scopeRegistry, context, null);
        scopeRegistry.registerFactory(Scope.SESSION, sessionFactory);
        scopeRegistry.registerFactory(Scope.CONVERSATION,
            new ConversationalScopeObjectFactory(scopeRegistry, context, null, null));
        ScopeContainer request = scopeRegistry.getScopeContainer(Scope.REQUEST);
        assertTrue(request instanceof RequestScopeContainer);
        assertSame(request, scopeRegistry.getScopeContainer(Scope.REQUEST));
        ScopeContainer session = scopeRegistry.getScopeContainer(Scope.SESSION);
        assertTrue(session instanceof HttpSessionScopeContainer);
        assertSame(session, scopeRegistry.getScopeContainer(Scope.SESSION));
        assertNotSame(request, session);
        ScopeContainer conversation = scopeRegistry.getScopeContainer(Scope.CONVERSATION);
        assertTrue(conversation instanceof ConversationalScopeContainer);
        assertSame(conversation, scopeRegistry.getScopeContainer(Scope.CONVERSATION));
        assertNotSame(session, conversation);
    }

    public void testDeregisterFactory() throws Exception {
        WorkContext context = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        RequestScopeObjectFactory factory = new RequestScopeObjectFactory(context, null);
        scopeRegistry.registerFactory(Scope.REQUEST, factory);
        scopeRegistry.deregisterFactory(Scope.REQUEST);
        assertNull(scopeRegistry.getScopeContainer(Scope.REQUEST));
        ConversationalScopeObjectFactory convFactory =
            new ConversationalScopeObjectFactory(scopeRegistry, context, null, null);
        scopeRegistry.registerFactory(Scope.CONVERSATION, convFactory);
        scopeRegistry.deregisterFactory(Scope.CONVERSATION);
        assertNull(scopeRegistry.getScopeContainer(Scope.CONVERSATION));
    }

    public void testScopeNotRegistered() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        assertNull(scopeRegistry.getScopeContainer(Scope.REQUEST));
        assertNull(scopeRegistry.getScopeContainer(Scope.SESSION));
        assertNull(scopeRegistry.getScopeContainer(Scope.CONVERSATION));
        assertNull(scopeRegistry.getScopeContainer(Scope.STATELESS));
    }


}
