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
package org.apache.tuscany.interfacedef.java.introspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.InvalidCallbackException;
import org.apache.tuscany.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.interfacedef.InvalidOperationException;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.OverloadedOperationException;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.interfacedef.impl.OperationImpl;
import org.apache.tuscany.interfacedef.java.JavaFactory;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.apache.tuscany.interfacedef.java.impl.DefaultJavaFactory;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Remotable;

/**
 * Default implementation of an InterfaceJavaIntrospector.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultJavaInterfaceIntrospector implements JavaInterfaceIntrospectorExtensionPoint {
    public static final String IDL_INPUT = "idl:input";

    private static final String UNKNOWN_DATABINDING = null;

    private JavaFactory javaFactory;
    private List<JavaInterfaceIntrospectorExtension> extensions = new ArrayList<JavaInterfaceIntrospectorExtension>();

    public DefaultJavaInterfaceIntrospector() {
        this.javaFactory = new DefaultJavaFactory();
    }

    public void addExtension(JavaInterfaceIntrospectorExtension extension) {
        extensions.add(extension);
    }

    public void removeExtension(JavaInterfaceIntrospectorExtension extension) {
        extensions.remove(extension);
    }

    public JavaInterface introspect(Class<?> type) throws InvalidInterfaceException {
        JavaInterface javaInterface = javaFactory.createJavaInterface();
        javaInterface.setJavaClass(type);

        boolean remotable = type.isAnnotationPresent(Remotable.class);
        javaInterface.setRemotable(remotable);
        
        boolean conversational = type.isAnnotationPresent(Conversational.class);
        javaInterface.setConversational(conversational);
        
        Class<?> callbackClass = null;
        org.osoa.sca.annotations.Callback callback = type.getAnnotation(org.osoa.sca.annotations.Callback.class);
        if (callback != null && !Void.class.equals(callback.value())) {
            callbackClass = callback.value();
        } else if (callback != null && Void.class.equals(callback.value())) {
            throw new InvalidCallbackException("No callback interface specified on annotation");
        }
        javaInterface.setCallbackClass(callbackClass);
        
        javaInterface.getOperations().addAll(getOperations(type, remotable, conversational));

        for (JavaInterfaceIntrospectorExtension extension : extensions) {
            extension.visitInterface(javaInterface);
        }
        return javaInterface;
    }

    private <T> List<Operation> getOperations(Class<T> type, boolean remotable, boolean conversational)
        throws InvalidInterfaceException {
        Method[] methods = type.getMethods();
        List<Operation> operations = new ArrayList<Operation>(methods.length);
        Set<String> names = remotable? new HashSet<String>() : null;
        for (Method method : methods) {
            String name = method.getName();
            if (remotable && names.contains(name)) {
                throw new OverloadedOperationException(method);
            }
            if(remotable) {
                names.add(name);
            }

            Class returnType = method.getReturnType();
            Class[] paramTypes = method.getParameterTypes();
            Class[] faultTypes = method.getExceptionTypes();
            boolean nonBlocking = method.isAnnotationPresent(OneWay.class);
            Operation.ConversationSequence conversationSequence = Operation.ConversationSequence.NO_CONVERSATION;
            if (method.isAnnotationPresent(EndsConversation.class)) {
                if (!conversational) {
                    throw new InvalidOperationException(
                                                        "Method is marked as end conversation but contract is not conversational",
                                                        method);
                }
                conversationSequence = Operation.ConversationSequence.CONVERSATION_END;
            } else if (conversational) {
                conversationSequence = Operation.ConversationSequence.CONVERSATION_CONTINUE;
            }

            // Set outputType to null for void
            DataType<Class> returnDataType = returnType == void.class ? null
                                                                     : new DataTypeImpl<Class>(UNKNOWN_DATABINDING,
                                                                                               returnType, returnType);
            List<DataType> paramDataTypes = new ArrayList<DataType>(paramTypes.length);
            for (Class paramType : paramTypes) {
                paramDataTypes.add(new DataTypeImpl<Class>(UNKNOWN_DATABINDING, paramType, paramType));
            }
            List<DataType> faultDataTypes = new ArrayList<DataType>(faultTypes.length);
            for (Class faultType : faultTypes) {
                // Only add checked exceptions
                if (Exception.class.isAssignableFrom(faultType) && (!RuntimeException.class.isAssignableFrom(faultType))) {
                    faultDataTypes.add(new DataTypeImpl<Class>(UNKNOWN_DATABINDING, faultType, faultType));
                }
            }

            DataType<List<DataType>> inputType = new DataTypeImpl<List<DataType>>(IDL_INPUT, Object[].class,
                                                                                  paramDataTypes);
            Operation operation = new OperationImpl(name);
            operation.setInputType(inputType);
            operation.setOutputType(returnDataType);
            operation.setFaultTypes(faultDataTypes);
            operation.setConversationSequence(conversationSequence);
            operation.setNonBlocking(nonBlocking);
            operations.add(operation);
        }
        return operations;
    }

}
