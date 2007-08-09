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

package org.apache.tuscany.sca.core.databinding.module;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.databinding.processor.DataBindingJavaInterfaceProcessor;
import org.apache.tuscany.sca.core.databinding.transformers.Exception2ExceptionTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.Input2InputTransformer;
import org.apache.tuscany.sca.core.databinding.transformers.Output2OutputTransformer;
import org.apache.tuscany.sca.core.databinding.wire.DataBindingRuntimeWireProcessor;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.databinding.DefaultTransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.TransformerExtensionPoint;
import org.apache.tuscany.sca.databinding.impl.Group2GroupTransformer;
import org.apache.tuscany.sca.databinding.impl.MediatorImpl;
import org.apache.tuscany.sca.databinding.javabeans.DOMNode2JavaBeanTransformer;
import org.apache.tuscany.sca.databinding.javabeans.JavaBean2DOMNodeTransformer;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.databinding.javabeans.SimpleJavaDataBinding;
import org.apache.tuscany.sca.databinding.xml.DOMDataBinding;
import org.apache.tuscany.sca.databinding.xml.InputSource2Node;
import org.apache.tuscany.sca.databinding.xml.InputSource2SAX;
import org.apache.tuscany.sca.databinding.xml.InputStream2Node;
import org.apache.tuscany.sca.databinding.xml.InputStream2SAX;
import org.apache.tuscany.sca.databinding.xml.Node2OutputStream;
import org.apache.tuscany.sca.databinding.xml.Node2SimpleJavaType;
import org.apache.tuscany.sca.databinding.xml.Node2String;
import org.apache.tuscany.sca.databinding.xml.Node2Writer;
import org.apache.tuscany.sca.databinding.xml.Node2XMLStreamReader;
import org.apache.tuscany.sca.databinding.xml.Reader2Node;
import org.apache.tuscany.sca.databinding.xml.Reader2SAX;
import org.apache.tuscany.sca.databinding.xml.SAX2DOMPipe;
import org.apache.tuscany.sca.databinding.xml.SimpleJavaType2Node;
import org.apache.tuscany.sca.databinding.xml.Source2ResultTransformer;
import org.apache.tuscany.sca.databinding.xml.StreamDataPipe;
import org.apache.tuscany.sca.databinding.xml.String2Node;
import org.apache.tuscany.sca.databinding.xml.String2SAX;
import org.apache.tuscany.sca.databinding.xml.String2XMLStreamReader;
import org.apache.tuscany.sca.databinding.xml.Writer2ReaderDataPipe;
import org.apache.tuscany.sca.databinding.xml.XMLGroupDataBinding;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2SAX;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2String;
import org.apache.tuscany.sca.databinding.xml.XMLStringDataBinding;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.RuntimeWireProcessorExtensionPoint;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingModuleActivator implements ModuleActivator {

    public Object[] getExtensionPoints() {
        return null;
    }

    public void start(ExtensionPointRegistry registry) {
        DataBindingExtensionPoint dataBindings = registry.getExtensionPoint(DataBindingExtensionPoint.class);
        TransformerExtensionPoint transformers = registry.getExtensionPoint(TransformerExtensionPoint.class);
        
        //FIXME hack for now
        ((DefaultTransformerExtensionPoint)transformers).setDataBindings(dataBindings);
        
        MediatorImpl mediator = new MediatorImpl(dataBindings, transformers);
        Input2InputTransformer input2InputTransformer = new Input2InputTransformer();
        input2InputTransformer.setMediator(mediator);

        Output2OutputTransformer output2OutputTransformer = new Output2OutputTransformer();
        output2OutputTransformer.setMediator(mediator);

        Exception2ExceptionTransformer exception2ExceptionTransformer = new Exception2ExceptionTransformer();
        exception2ExceptionTransformer.setMediator(mediator);

        transformers.addTransformer(input2InputTransformer);
        transformers.addTransformer(output2OutputTransformer);
        transformers.addTransformer(exception2ExceptionTransformer);

        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        JavaInterfaceFactory javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        javaFactory.addInterfaceVisitor(new DataBindingJavaInterfaceProcessor(dataBindings));

        RuntimeWireProcessorExtensionPoint wireProcessorExtensionPoint = registry
            .getExtensionPoint(RuntimeWireProcessorExtensionPoint.class);
        if (wireProcessorExtensionPoint != null) {
            wireProcessorExtensionPoint.addWireProcessor(new DataBindingRuntimeWireProcessor(mediator));
        }

        DOMDataBinding domDataBinding = new DOMDataBinding();
        dataBindings.addDataBinding(domDataBinding);
        XMLStringDataBinding xmlStringDataBinding = new XMLStringDataBinding();
        dataBindings.addDataBinding(xmlStringDataBinding);
        XMLGroupDataBinding xmlGroupDataBinding = new XMLGroupDataBinding();
        dataBindings.addDataBinding(xmlGroupDataBinding);
        JavaBeansDataBinding javaBeansDataBinding = new JavaBeansDataBinding();
        dataBindings.addDataBinding(javaBeansDataBinding);
        
        SimpleJavaDataBinding simpleJavaDataBinding = new SimpleJavaDataBinding();
        dataBindings.addDataBinding(simpleJavaDataBinding);

        Group2GroupTransformer group2GroupTransformer = new Group2GroupTransformer();
        group2GroupTransformer.setMediator(mediator);
        transformers.addTransformer(group2GroupTransformer);

        transformers.addTransformer(new InputSource2Node());
        transformers.addTransformer(new InputSource2SAX());
        transformers.addTransformer(new InputStream2Node());
        transformers.addTransformer(new InputStream2SAX());

        transformers.addTransformer(new DOMNode2JavaBeanTransformer());
        transformers.addTransformer(new Node2OutputStream());
        transformers.addTransformer(new Node2String());
        transformers.addTransformer(new Node2Writer());
        transformers.addTransformer(new Node2XMLStreamReader());

        transformers.addTransformer(new JavaBean2DOMNodeTransformer());
        transformers.addTransformer(new Reader2Node());

        transformers.addTransformer(new Reader2SAX());
        transformers.addTransformer(new SAX2DOMPipe());

        transformers.addTransformer(new Source2ResultTransformer());
        transformers.addTransformer(new StreamDataPipe());
        transformers.addTransformer(new String2Node());
        transformers.addTransformer(new String2SAX());
        transformers.addTransformer(new String2XMLStreamReader());
        transformers.addTransformer(new Writer2ReaderDataPipe());

        transformers.addTransformer(new XMLStreamReader2Node());
        transformers.addTransformer(new XMLStreamReader2SAX());
        transformers.addTransformer(new XMLStreamReader2String());
        
        transformers.addTransformer(new Node2SimpleJavaType());
        transformers.addTransformer(new SimpleJavaType2Node());
    }

    public void stop(ExtensionPointRegistry registry) {
    }
}
