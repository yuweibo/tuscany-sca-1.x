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

package org.apache.tuscany.sca.contribution.java;

/**
 * An extension point for contribution classloaders. Contribution 
 * classloaders respect the classloading strategy for the contribution
 * in question. For example, a JAR contribution loads classes from its 
 * root directory while an EAR contribution follows the JEE classloading 
 * strategy. The choice of classloader is driven by the type of 
 * contribution being loaded. 
 *
 * @version $Rev$ $Date$
 */
public interface ContributionClassloaderProviderExtensionPoint {
    
    /**
     * Add a contribution classloader provider extension.
     * 
     * @param provider The provider to add
     */
    void addProvider(ContributionClassLoaderProvider provider);
    
    /**
     * Remove a contribution classloader provider extension.
     *  
     * @param provider The provider to remove
     */
    void removeProvider(ContributionClassLoaderProvider provider); 
    
    /**
     * Get a contribution classloader provider for the given contribution type.
     * @param contributionType the lookup key 
     * @return The provider
     */
    ContributionClassLoaderProvider getProvider(String contributionType);

}
