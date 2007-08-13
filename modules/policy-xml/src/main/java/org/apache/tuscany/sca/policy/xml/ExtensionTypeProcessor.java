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

package org.apache.tuscany.sca.policy.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.policy.BindingType;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.ExtensionTypeFactory;
import org.apache.tuscany.sca.policy.ImplementationType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicyFactory;


/* 
 * Processor for handling xml models of ExtensionType meta data definitions
 */
public abstract class ExtensionTypeProcessor<T extends ExtensionType> implements StAXArtifactProcessor<T>, PolicyConstants {

    protected ExtensionTypeFactory extnTypeFactory;
    protected PolicyFactory policyFactory; 
    protected StAXArtifactProcessor<Object> extensionProcessor;
    

    public ExtensionTypeProcessor(PolicyFactory policyFactory, ExtensionTypeFactory extnTypeFactory, StAXArtifactProcessor<Object> extensionProcessor) {
        this.policyFactory = policyFactory;
        this.extnTypeFactory = extnTypeFactory;
        this.extensionProcessor = extensionProcessor;
    }

    protected void readAlwaysProvidedIntents(ExtensionType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, ALWAYS_PROVIDES);
        if (value != null) {
            List<Intent> alwaysProvided = extnType.getAlwaysProvidedIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                alwaysProvided.add(intent);
            }
        }
    }
    
    protected void readMayProvideIntents(ExtensionType extnType, XMLStreamReader reader) {
        String value = reader.getAttributeValue(null, MAY_PROVIDE);
        if (value != null) {
            List<Intent> mayProvide = extnType.getMayProvideIntents();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                QName qname = getQNameValue(reader, tokens.nextToken());
                Intent intent = policyFactory.createIntent();
                intent.setName(qname);
                mayProvide.add(intent);
            }
        }
    }
  
    public void write(T extnType, XMLStreamWriter writer) throws ContributionWriteException {
        try {
            // Write an <sca:bindingType or sca:implementationType>
            if ( extnType instanceof BindingType ) {
                writer.writeStartElement(SCA10_NS, BINDING_TYPE);
            } else if ( extnType instanceof ImplementationType ) {
                writer.writeStartElement(SCA10_NS, IMPLEMENATION_TYPE);
            }
            
            writeAlwaysProvidesIntentsAttribute(extnType, writer);
            writeMayProvideIntentsAttribute(extnType, writer);
            
            writer.writeEndElement();
            
        } catch (XMLStreamException e) {
            throw new ContributionWriteException(e);
        }
    }
    
    protected void writeMayProvideIntentsAttribute(ExtensionType extnType, XMLStreamWriter writer) throws XMLStreamException {
        StringBuffer sb  = new StringBuffer();
        for ( Intent intent : extnType.getMayProvideIntents() ) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }
        
        if ( sb.length() > 0 ) {
            writer.writeAttribute(MAY_PROVIDE, sb.toString());
        }
    }
    
    protected void writeAlwaysProvidesIntentsAttribute(ExtensionType extnType, XMLStreamWriter writer) throws XMLStreamException {
        StringBuffer sb  = new StringBuffer();
        for ( Intent intent : extnType.getAlwaysProvidedIntents() ) {
            writer.writeNamespace(intent.getName().getPrefix(), intent.getName().getNamespaceURI());
            sb.append(intent.getName().getPrefix() + COLON + intent.getName().getLocalPart());
            sb.append(WHITE_SPACE);
        }
        
        if ( sb.length() > 0 ) {
            writer.writeAttribute(ALWAYS_PROVIDES, sb.toString());
            
        }
    }
    
    private void resolveExtensionType(T extnType, ModelResolver resolver) throws ContributionResolveException {
        //FIXME: need to resolve the binding and implementations across the assembly model
        extnType.setUnresolved(false);
    }
    
    public void resolve(T extnType, ModelResolver resolver) throws ContributionResolveException {
        resolveExtensionType(extnType, resolver);

        if ( !extnType.isUnresolved() ) {
             resolver.addModel(extnType);
        }
    }

    private void resolveAlwaysProvidedIntents(ExtensionType extnType, ModelResolver resolver) throws ContributionResolveException {
        boolean isUnresolved = false;
        if (extnType != null && extnType.isUnresolved()) {
            //resolve alwaysProvided Intents
            List<Intent> alwaysProvided = new ArrayList<Intent>(); 
            for ( Intent providedIntent : extnType.getAlwaysProvidedIntents() ) {
                if ( providedIntent.isUnresolved() ) {
                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
                    alwaysProvided.add(providedIntent);
                    if (providedIntent.isUnresolved()) {
                        isUnresolved = true;
                    }
                }
            }
            extnType.getAlwaysProvidedIntents().clear();
            extnType.getAlwaysProvidedIntents().addAll(alwaysProvided);
        }
        extnType.setUnresolved(isUnresolved);
    }
    
    private void resolveMayProvideIntents(ExtensionType extnType, ModelResolver resolver) throws ContributionResolveException {
        boolean isUnresolved = false;
        if (extnType != null && extnType.isUnresolved()) {
            //resolve may provide Intents
            List<Intent> mayProvide = new ArrayList<Intent>(); 
            for ( Intent providedIntent : extnType.getMayProvideIntents() ) {
                if ( providedIntent.isUnresolved() ) {
                    providedIntent = resolver.resolveModel(Intent.class, providedIntent);
                    mayProvide.add(providedIntent);
                    if (providedIntent.isUnresolved()) {
                        isUnresolved = true;
                    }
                }
            }
            extnType.getAlwaysProvidedIntents().clear();
            extnType.getAlwaysProvidedIntents().addAll(mayProvide);
        }
        extnType.setUnresolved(isUnresolved);
    }
    
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }
    
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }
    

    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }
}
