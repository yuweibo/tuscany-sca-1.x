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
package org.apache.tuscany.spi.model.physical;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tuscany.spi.model.ModelObject;

/**
 * Aggregates a collection of operations.
 * 
 * @version $Revison$ $Date$
 *
 */
public class Operations extends ModelObject {
    
    // Collection of operations
    private Set<PhysicalOperationDefinition> operations = new HashSet<PhysicalOperationDefinition>();

    /**
     * Returns a read-only view of the available operations.
     * @return Collection of operations.
     */
    public Set<PhysicalOperationDefinition> getOperations() {
        return Collections.unmodifiableSet(operations);
    }

    /**
     * Adds an operation definition.
     * @param operation Operation to be added.
     */
    public void addOperation(PhysicalOperationDefinition operation) {
        operations.add(operation);
    }

}
