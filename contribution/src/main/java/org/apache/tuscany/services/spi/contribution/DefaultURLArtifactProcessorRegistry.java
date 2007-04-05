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
package org.apache.tuscany.services.spi.contribution;

import java.net.URL;

/**
 * The default implementation of a StAX artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultURLArtifactProcessorRegistry
    extends DefaultArtifactProcessorRegistry<URL, Object, String>
    implements URLArtifactProcessorRegistry {

    /**
     * Constructs a new loader registry.
     * @param assemblyFactory
     * @param policyFactory
     * @param factory
     */
    public DefaultURLArtifactProcessorRegistry() {
    }

    public Object read(URL source) throws ContributionReadException {
        
        // Delegate to the processor associated with file extension
        String extension = source.getFile();
        extension = extension.substring(extension.lastIndexOf('.'));
        URLArtifactProcessor<Object> processor = (URLArtifactProcessor<Object>)this.getProcessor(extension);
        if (processor == null) {
            return null;
        }
        return processor.read(source);
    }
    
    public void resolve(Object model, ArtifactResolver resolver) throws ContributionException {

        // Delegate to the processor associated with the model type
        URLArtifactProcessor<Object> processor = (URLArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
        if (processor != null) {
            processor.resolve(model, resolver);
        }
    }
    
    public void optimize(Object model) throws ContributionException {

        // Delegate to the processor associated with the model type
        URLArtifactProcessor<Object> processor = (URLArtifactProcessor<Object>)this.getProcessor((Class<Object>)model.getClass());
        if (processor != null) {
            processor.optimize(model);
        }
    }
    
    public <MO> MO read(URL url, Class<MO> type) throws ContributionReadException {
        Object mo = read(url);
        if (type.isInstance(mo)) {
            return type.cast(mo);
        } else {
            UnrecognizedElementException e = new UnrecognizedElementException(null);
            e.setResourceURI(url.toString());
            throw e;
        }
    }

}
