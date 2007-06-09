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
package org.apache.tuscany.sca.implementation.bpel.impl;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementation;
import org.apache.tuscany.sca.implementation.bpel.BPELImplementationFactory;

/**
 * Implements a STAX artifact processor for BPEL implementations.
 * 
 * The artifact processor is responsible for processing <implementation.bpel>
 * elements in SCA assembly XML composite files and populating the BPEL
 * implementation model, resolving its references to other artifacts in the SCA
 * contribution, and optionally write the model back to SCA assembly XML.
 * 
 *  @version $Rev$ $Date$
 */
public class BPELImplementationProcessor implements StAXArtifactProcessor<BPELImplementation> {
    private static final QName IMPLEMENTATION_BPEL = new QName("http://bpel", "implementation.bpel");
    
    private BPELImplementationFactory bpelFactory;
    
    public BPELImplementationProcessor(BPELImplementationFactory crudFactory) {
        this.bpelFactory = crudFactory;
    }

    public QName getArtifactType() {
        // Returns the qname of the XML element processed by this processor
        return IMPLEMENTATION_BPEL;
    }

    public Class<BPELImplementation> getModelType() {
        // Returns the type of model processed by this processor
        return BPELImplementation.class;
    }

    public BPELImplementation read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPLEMENTATION_BPEL.equals(reader.getName());
        
        // Read an <implementation.bpel> element
        try {
            // Read the process attribute. 
            String process = reader.getAttributeValue(null, "process");
            
            // Create an initialize the BPEL implementation model
            BPELImplementation implementation = bpelFactory.createBPELImplementation();
            implementation.setProcess(process);
            
            // Skip to end element
            while (reader.hasNext()) {
                if (reader.next() == END_ELEMENT && IMPLEMENTATION_BPEL.equals(reader.getName())) {
                    break;
                }
            }
            
            return implementation;
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(BPELImplementation impl, ModelResolver resolver) throws ContributionResolveException {
    }

    public void write(BPELImplementation model, XMLStreamWriter outputSource) throws ContributionWriteException {
    }
}
