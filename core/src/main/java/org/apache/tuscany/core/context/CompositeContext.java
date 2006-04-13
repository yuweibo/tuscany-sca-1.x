package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Extensible;

import java.util.List;

/**
 * A context which contains child component contexts.
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeContext extends Context {

    /**
     * Returns the parent context, or null if the context does not have one
     */
    public CompositeContext getParent();

    /**
     * Propagates an event to registered listeners. All lifecycle events will be propagated to children in the order
     * that they were registered. Listeners are expected to be well-behaved and if an exception is thrown the
     * notification process will be aborted.
     *
     * @param pEventType the type of event. Basic types are defined in {@link EventContext}
     * @param pMessage the message associated with the event or null
     * @throws EventException if an error occurs while sending the event
     */
    //public void fireEvent(int pEventType, Object pMessage) throws EventException;

    /**
     * Adds runtime artifacts represented by the set of model objects to the composite context by merging them with
     * existing artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    public void registerModelObjects(List<? extends Extensible> models) throws ConfigurationException;

    /**
     * Adds a runtime artifact represented by the model object to the composite context by merging it with existing
     * artifacts. Implementing classes may support only a subset of {@link Part} types.
     * 
     * @see org.apache.tuscany.model.assembly.Component
     * @see org.apache.tuscany.model.assembly.ModuleComponent
     * @see org.apache.tuscany.model.assembly.AtomicComponent
     * @see org.apache.tuscany.model.assembly.EntryPoint
     * @see org.apache.tuscany.model.assembly.ExternalService
     */
    public void registerModelObject(Extensible model) throws ConfigurationException;

    /**
     * Returns the child context associated with a given name
     */
    public Context getContext(String name);

    /**
     * Intended for internal use by the runtime, returns an implementation instance for the given context name, which
     * may be a compound component/service form. Unlike {@link Context#getInstance(QualifiedName)}, which for composite contexts only returns
     * entry point proxies, this method will return any type of contained implementation instance.
     * 
     * @throws TargetException if there was an error returning the instance
     */
    //public Object locateInstance(QualifiedName name) throws TargetException;

    /**
     * Returns the composite managed by this composite context
     */
    @Deprecated
    public Composite getComposite();
    
}
