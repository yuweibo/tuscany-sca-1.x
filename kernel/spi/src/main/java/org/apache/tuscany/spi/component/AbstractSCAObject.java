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
package org.apache.tuscany.spi.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;

/**
 * Functionality common to all <code>SCAObject<code> implementations
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractSCAObject extends AbstractLifecycle implements SCAObject {
    protected static final EventFilter TRUE_FILTER = new TrueFilter();

    protected Map<EventFilter, List<RuntimeEventListener>> listeners;
    protected final CompositeComponent parent;
    private final String name;
    private final Map<Object, Object> extensions = new HashMap<Object, Object>();
    private String cannonicalName;

    public AbstractSCAObject(String name, CompositeComponent parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getCanonicalName() {
        if (cannonicalName == null) {
            StringBuffer b = new StringBuffer(name);
            if (parent != null) {
                b.insert(0, parent.getCanonicalName() + "/");
            }
            cannonicalName = b.toString();
        }
        return cannonicalName;
    }

    public CompositeComponent getParent() {
        return parent;
    }

    public void addListener(RuntimeEventListener listener) {
        addListener(TRUE_FILTER, listener);
    }

    public void removeListener(RuntimeEventListener listener) {
        assert listener != null : "Listener cannot be null";
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
        assert listener != null : "Listener cannot be null";
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
        assert event != null : "Event object was null";
        for (Map.Entry<EventFilter, List<RuntimeEventListener>> entry : getListeners().entrySet()) {
            if (entry.getKey().match(event)) {
                for (RuntimeEventListener listener : entry.getValue()) {
                    listener.onEvent(event);
                }
            }
        }
    }

    protected Map<EventFilter, List<RuntimeEventListener>> getListeners() {
        if (listeners == null) {
            listeners = new ConcurrentHashMap<EventFilter, List<RuntimeEventListener>>();
        }
        return listeners;
    }

    public void prepare() {

    }

    public String toString() {
        return "[" + name + "] in state [" + super.toString() + ']';
    }

    public Map<Object, Object> getExtensions() {
        return extensions;
    }

    public boolean isSystem() {
        return false;
    }
}
