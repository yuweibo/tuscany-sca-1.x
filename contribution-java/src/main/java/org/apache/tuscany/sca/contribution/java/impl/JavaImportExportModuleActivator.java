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

package org.apache.tuscany.sca.contribution.java.impl;

import org.apache.tuscany.sca.contribution.java.JavaImportExportFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionListenerExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;

/**
 * Java Import Export module activator
 * Responsible for registering all extension points provide by the module such as :
 *    - Import/Export processors
 *    - ClassReferenceModelResolver
 *    - Import/Export listeners
 * 
 * @version $Rev$ $Date$
 */
public class JavaImportExportModuleActivator implements ModuleActivator {
    private static final JavaImportExportFactory factory = new JavaImportExportFactoryImpl();
    
    /**
     * Artifact processors for <import.java>
     */
    private JavaImportProcessor importProcessor;

    /**
     * Artifact processors for <export.java>
     */
    private JavaExportProcessor exportProcessor;
        
    /**
     * Java Import/Export listener
     */
    private JavaImportExportListener listener;

    public void start(ExtensionPointRegistry registry) {
        importProcessor = new JavaImportProcessor(factory);
        exportProcessor = new JavaExportProcessor(factory);

        //register artifact processors for java import/export
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.addArtifactProcessor(importProcessor);
        processors.addArtifactProcessor(exportProcessor);
        
        //register artifact model resolvers for class reference supporting import/export
        ModelResolverExtensionPoint resolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolvers.addResolver(ClassReference.class, ClassReferenceModelResolver.class);
        
        //register contribution listener responsible for initializing import/export model resolvers
        ContributionListenerExtensionPoint listeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class);
        listeners.addContributionListener(new JavaImportExportListener());
    }

    public void stop(ExtensionPointRegistry registry) {
        //unregister artifact processors
        StAXArtifactProcessorExtensionPoint processors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        processors.removeArtifactProcessor(importProcessor);
        processors.removeArtifactProcessor(exportProcessor);
        
        //unregister artifact model resolvers
        ModelResolverExtensionPoint resolvers = registry.getExtensionPoint(ModelResolverExtensionPoint.class);
        resolvers.removeResolver(ClassReference.class);
        
        //unregister contribution listener
        ContributionListenerExtensionPoint listeners = registry.getExtensionPoint(ContributionListenerExtensionPoint.class);
        listeners.removeContributionListener(listener);
    }
}
