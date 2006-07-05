package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends MockObjectTestCase {

    CompositeComponent parent;

    public void testModuleScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Module.class, type, null);
        assertEquals(Scope.MODULE, type.getLifecycleScope());
    }

    public void testSessionScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Session.class, type, null);
        assertEquals(Scope.SESSION, type.getLifecycleScope());
    }

    public void testRequestScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Request.class, type, null);
        assertEquals(Scope.REQUEST, type.getLifecycleScope());
    }

    public void testCompositeScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Composite.class, type, null);
        assertEquals(Scope.COMPOSITE, type.getLifecycleScope());
    }

    public void testStatelessScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, Stateless.class, type, null);
        assertEquals(Scope.STATELESS, type.getLifecycleScope());
    }

    public void testNoScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(parent, None.class, type, null);
        assertEquals(Scope.STATELESS, type.getLifecycleScope());
    }

    protected void setUp() throws Exception {
        super.setUp();
        Mock mock = mock(CompositeComponent.class);
        parent = (CompositeComponent) mock.proxy();
    }

    @org.osoa.sca.annotations.Scope("MODULE")
    private class Module {
    }

    @org.osoa.sca.annotations.Scope("SESSION")
    private class Session {
    }

    @org.osoa.sca.annotations.Scope("REQUEST")
    private class Request {
    }

    @org.osoa.sca.annotations.Scope("COMPOSITE")
    private class Composite {
    }

    @org.osoa.sca.annotations.Scope("STATELESS")
    private class Stateless {
    }

    private class None {
    }

}
