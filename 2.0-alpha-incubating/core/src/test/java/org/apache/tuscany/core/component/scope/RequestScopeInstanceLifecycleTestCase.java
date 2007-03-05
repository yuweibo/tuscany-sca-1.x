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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.mock.component.OrderedInitPojo;
import org.apache.tuscany.core.mock.component.OrderedInitPojoImpl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

/**
 * Lifecycle unit tests for the request scope container
 *
 * @version $Rev$ $Date$
 */
public class RequestScopeInstanceLifecycleTestCase extends TestCase {

    public void testInitDestroy() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx, null);
        scope.start();
        Foo comp = new Foo();
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstance()).andReturn(comp);
        component.init(EasyMock.eq(comp));
        component.destroy(EasyMock.eq(comp));
        EasyMock.replay(component);
        scope.register(component);
        scope.onEvent(new RequestStart(this));
        assertNotNull(scope.getInstance(component));
        // expire
        scope.onEvent(new RequestEnd(this));
        scope.stop();
        EasyMock.verify(component);
        scope.stop();
    }

    public void testDestroyOrder() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        RequestScopeContainer scope = new RequestScopeContainer(ctx, null);
        scope.start();

        AtomicComponent oneComponent = createComponent();
        scope.register(oneComponent);
        AtomicComponent twoComponent = createComponent();
        scope.register(twoComponent);
        AtomicComponent threeComponent = createComponent();
        scope.register(threeComponent);

        scope.onEvent(new RequestStart(this));
        OrderedInitPojo one = (OrderedInitPojo) scope.getInstance(oneComponent);
        assertNotNull(one);
        assertEquals(1, one.getNumberInstantiated());
        assertEquals(1, one.getInitOrder());

        OrderedInitPojo two = (OrderedInitPojo) scope.getInstance(twoComponent);
        assertNotNull(two);
        assertEquals(2, two.getNumberInstantiated());
        assertEquals(2, two.getInitOrder());

        OrderedInitPojo three = (OrderedInitPojo) scope.getInstance(threeComponent);
        assertNotNull(three);
        assertEquals(3, three.getNumberInstantiated());
        assertEquals(3, three.getInitOrder());

        scope.onEvent(new RequestEnd(this));
        assertEquals(0, one.getNumberInstantiated());
        scope.stop();
        EasyMock.verify(oneComponent);
        EasyMock.verify(twoComponent);
        EasyMock.verify(threeComponent);
    }

    @SuppressWarnings("unchecked")
    private AtomicComponent createComponent() throws TargetException {
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.createInstance()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return new OrderedInitPojoImpl();
            }
        });
        component.init(EasyMock.isA(OrderedInitPojoImpl.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                OrderedInitPojoImpl pojo = (OrderedInitPojoImpl) EasyMock.getCurrentArguments()[0];
                pojo.init();
                return null;
            }
        });
        component.destroy(EasyMock.isA(OrderedInitPojoImpl.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                OrderedInitPojoImpl pojo = (OrderedInitPojoImpl) EasyMock.getCurrentArguments()[0];
                pojo.destroy();
                return null;
            }
        });
        EasyMock.replay(component);
        return component;
    }

    private class Foo {

    }

}
