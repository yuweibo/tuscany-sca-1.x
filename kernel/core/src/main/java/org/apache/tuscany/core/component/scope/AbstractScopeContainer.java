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


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.PersistenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;

/**
 * Implements functionality common to scope contexts.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractScopeContainer<KEY> extends AbstractLifecycle implements ScopeContainer<KEY> {
    private static final EventFilter TRUE_FILTER = new TrueFilter();

    protected WorkContext workContext;
    protected ScopeContainerMonitor monitor;
    private Map<EventFilter, List<RuntimeEventListener>> listeners;

    public AbstractScopeContainer(WorkContext workContext, ScopeContainerMonitor monitor) {
        this.workContext = workContext;
        this.monitor = monitor;
    }

    public void register(AtomicComponent component) {
        checkInit();
    }

    public void unregister(AtomicComponent component) {
    }

    public void startContext(KEY contextId) {
    }

    public void stopContext(KEY contextId) {
    }

    public void addListener(RuntimeEventListener listener) {
        addListener(TRUE_FILTER, listener);
    }

    public void removeListener(RuntimeEventListener listener) {
        assert listener != null;
        synchronized (getListeners()) {
            for (List<RuntimeEventListener> currentList : getListeners().values()) {
                for (RuntimeEventListener current : currentList) {
                    if (current == listener) {
                        currentList.remove(current);
                        return;
                    }
                }
            }
        }
    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {
        assert listener != null;
        synchronized (getListeners()) {
            List<RuntimeEventListener> list = getListeners().get(filter);
            if (list == null) {
                list = new CopyOnWriteArrayList<RuntimeEventListener>();
                listeners.put(filter, list);
            }
            list.add(listener);
        }
    }

    public void publish(Event event) {
        assert event != null;
        for (Map.Entry<EventFilter, List<RuntimeEventListener>> entry : getListeners().entrySet()) {
            if (entry.getKey().match(event)) {
                for (RuntimeEventListener listener : entry.getValue()) {
                    listener.onEvent(event);
                }
            }
        }
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component, KEY contextId)
        throws TargetResolutionException {
        return null;
    }

    public <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException {
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent component) throws TargetResolutionException {
        return getInstanceWrapper(component, true);
    }

    public <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component) throws TargetResolutionException {
        InstanceWrapper<T> wrapper = getInstanceWrapper(component, false);
        if (wrapper == null) {
            throw new TargetNotFoundException(component.getUri().toString());
        }
        return wrapper;
    }

    public <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper)
        throws TargetDestructionException {
    }

    public void persistNew(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");

    }

    public void persist(AtomicComponent component, String id, Object instance, long expiration)
        throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    public void remove(AtomicComponent component) throws PersistenceException {
        throw new UnsupportedOperationException("Scope does not support persistence");
    }

    protected Map<EventFilter, List<RuntimeEventListener>> getListeners() {
        if (listeners == null) {
            listeners = new ConcurrentHashMap<EventFilter, List<RuntimeEventListener>>();
        }
        return listeners;
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
    }

    protected WorkContext getWorkContext() {
        return workContext;
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    protected abstract <T> InstanceWrapper<T> getInstanceWrapper(AtomicComponent component, boolean create)
        throws TargetResolutionException;
}
