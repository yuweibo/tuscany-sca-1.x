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
package org.apache.tuscany.binding.axis2;

import static org.osoa.sca.Constants.SCA_NS;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.WSDL11ToAxisServiceBuilder;
import org.apache.axis2.description.WSDLToAxisServiceBuilder;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.axis2.wsdl.WSDLConstants.WSDL20_2004Constants;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.binding.ws.WebServiceBinding;
import org.apache.tuscany.http.ServletHostExtensionPoint;
import org.apache.tuscany.interfacedef.InterfaceContract;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.Operation.ConversationSequence;
import org.apache.tuscany.spi.Scope;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.TargetInvokerCreationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.component.WorkContextTunnel;
import org.apache.tuscany.spi.extension.ServiceBindingExtension;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.osoa.sca.annotations.Destroy;

/**
 * An implementation of a {@link ServiceBindingExtension} configured with the Axis2 binding
 */
public class Axis2ServiceBinding extends ServiceBindingExtension {

    private InterfaceContract serviceContract;
    private ConfigurationContext configContext;
    private WebServiceBinding wsBinding;
    private Map<Object, InvocationContext> invCtxMap = new HashMap<Object, InvocationContext>();
    private String serviceName;
//    private WorkContext workContext;
    private Set<String> seenConversations = Collections.synchronizedSet(new HashSet<String>());
    private ServletHostExtensionPoint servletHost;

    private static final QName BINDING_WS = new QName(SCA_NS, "binding.ws");

    public Axis2ServiceBinding(URI uri,
                               InterfaceContract serviceContract,
                               InterfaceContract serviceBindingContract,
                               WebServiceBinding wsBinding,
                               ServletHostExtensionPoint servletHost,
                               ConfigurationContext configContext,
                               WorkContext workContext) {

        super(uri);

        this.serviceContract = serviceContract;
        this.bindingServiceContract = serviceBindingContract;
        this.wsBinding = wsBinding;
        this.servletHost = servletHost;
        this.configContext = configContext;
        this.serviceName = uri.toString(); // TODO: whats this for, better name
//        this.workContext = workContext;

        start(); // TODO: hack while start isn't getting called by runtime 
    }

    public void start() {
        super.start();

        try {
            configContext.getAxisConfiguration().addService(createAxisService(wsBinding));
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }

        Axis2ServiceServlet servlet = new Axis2ServiceServlet();
        servlet.init(configContext);
        configContext.setContextRoot(getUri().toString());
        servletHost.addServletMapping(8080, getUri().getPath(), servlet);
    }

    @Destroy
    public void stop() {
        servletHost.removeServletMapping(8080, getUri().getPath());
        try {
            configContext.getAxisConfiguration().removeService(getUri().toString());
        } catch (AxisFault e) {
            throw new Axis2BindingRunTimeException(e);
        }
        super.stop();
    }

    private AxisService createAxisService(WebServiceBinding wsBinding) throws AxisFault {
        Definition definition = wsBinding.getWSDLDefinition().getDefinition();
        WebServicePortMetaData wsdlPortInfo =
            new WebServicePortMetaData(definition, wsBinding.getPort(), null, false);

        // TODO investigate if this is 20 wsdl what todo?
        WSDLToAxisServiceBuilder builder =
            new WSDL11ToAxisServiceBuilder(definition, wsdlPortInfo.getServiceName(), wsdlPortInfo.getPort()
                .getName());
        builder.setServerSide(true);
        AxisService axisService = builder.populateService();

        String path = getUri().getPath();
        if (path != null && path.length() > 1 && path.startsWith("/")) {
            path = path.substring(1);
        }
        axisService.setName(path);
        axisService.setServiceDescription("Tuscany configured AxisService for service: " + getUri().toString());

        // Use the existing WSDL
        Parameter wsdlParam = new Parameter(WSDLConstants.WSDL_4_J_DEFINITION, null);
        wsdlParam.setValue(definition);
        axisService.addParameter(wsdlParam);
        Parameter userWSDL = new Parameter("useOriginalwsdl", "true");
        axisService.addParameter(userWSDL);

        PortType wsdlPortType = wsdlPortInfo.getPortType();
        for (Object o : wsdlPortType.getOperations()) {
            javax.wsdl.Operation wsdlOperation = (javax.wsdl.Operation) o;
            String operationName = wsdlOperation.getName();

            Operation op = getOperation(operationName);
            if (op != null) {
                AxisOperation axisOp = axisService.getOperation(new QName(definition.getTargetNamespace(), operationName));

                if (op.isNonBlocking()) {
                    axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_ONLY);
                } else {
                    axisOp.setMessageExchangePattern(WSDL20_2004Constants.MEP_URI_IN_OUT);
                }

                MessageReceiver msgrec = null;
                if (serviceContract.getCallbackInterface() != null) {
                    msgrec = new Axis2ServiceInOutAsyncMessageReceiver(this, op);
                } else if (op.isNonBlocking()) {
                    msgrec = new Axis2ServiceInMessageReceiver(this, op);
                } else {
                    msgrec = new Axis2ServiceInOutSyncMessageReceiver(this, op);
                }
                axisOp.setMessageReceiver(msgrec);
            }
        }

        return axisService;
    }

    protected Operation getOperation(String operationName) {
        for (Operation op : serviceContract.getInterface().getOperations()) {
           if (op.getName().equalsIgnoreCase(operationName)) {
               return op;
           }
        }
        return null;
    }

    public Object invokeTarget(Operation op, Object[] args, Object messageId, String conversationID) throws InvocationTargetException {
        InvocationChain chain = null;
        for (InvocationChain ic : wire.getInvocationChains()) {
            if (ic.getSourceOperation().equals(op)) {
                chain = ic;
            }
        }
        if (chain == null) {
            throw new IllegalStateException("no InvocationChain on wire for operation " + op);
        }
        
        Interceptor headInterceptor = chain.getHeadInterceptor();
        WorkContext workContext = WorkContextTunnel.getThreadWorkContext();
        String oldConversationID = (String) workContext.getIdentifier(Scope.CONVERSATION);
        if (isConversational() && conversationID != null) {
            workContext.setIdentifier(Scope.CONVERSATION, conversationID);
        } else {
            workContext.clearIdentifier(Scope.CONVERSATION);
        }
        try {
            if (headInterceptor == null) {
                // short-circuit the dispatch and invoke the target directly
                TargetInvoker targetInvoker = chain.getTargetInvoker();
                if (targetInvoker == null) {
                    throw new AssertionError("No target invoker [" + chain.getTargetOperation().getName() + "]");
                }
                return targetInvoker.invokeTarget(args, TargetInvoker.NONE, null);
            } else {

                Message msg = new MessageImpl();
                msg.setTargetInvoker(chain.getTargetInvoker());
//                msg.pushFromAddress(getFromAddress()); // TODO : method gone in the TRUNK???
                if (messageId != null) {
                    msg.setMessageId(messageId);
                }
                msg.setBody(args);
                msg.setWorkContext(workContext);

                Message resp;

                if (isConversational()) {


                    ConversationSequence opSeq = op.getConversationSequence();
                    if (opSeq == ConversationSequence.CONVERSATION_END) {
                        assert seenConversations
                            .contains(conversationID) : "End of conversation called when no conversation existed";
                        msg.setConversationSequence(TargetInvoker.END);
                        seenConversations.remove(conversationID); //if a fault occurs does the conversation end?
                        //how do I know if a component called locally another opeation that ended this conversation?

                    } else {
                        boolean ec = seenConversations.contains(conversationID);
                        if (ec) {

                            msg.setConversationSequence(TargetInvoker.CONTINUE);
                        } else {
                            seenConversations.add(conversationID);
                            msg.setConversationSequence(TargetInvoker.START);
                        }
                    }

                }
                // dispatch the wire down the chain and get the response
                resp = headInterceptor.invoke(msg);
                Object body = resp.getBody();
                if (resp.isFault()) {
                    throw new InvocationTargetException((Throwable) body);
                }
                return body;
            }
        } finally {
            if (null != oldConversationID) {
                workContext.setIdentifier(Scope.CONVERSATION, conversationID);
            } else {
                workContext.clearIdentifier(Scope.CONVERSATION);
            }
        }
    }

    protected Object getFromAddress() {
        return this.serviceName;
    }

    /**
     * Get the Method from an interface matching the WSDL operation name
     */
    protected Method getMethod(Class<?> serviceInterface, String operationName) throws BuilderConfigException {
        // Note: this doesn't support overloaded operations
        Method[] methods = serviceInterface.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(operationName)) {
                return m;
            }
            // tolerate WSDL with capatalized operation name
            StringBuilder sb = new StringBuilder(operationName);
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
            if (m.getName().equals(sb.toString())) {
                return m;
            }
        }
        throw new BuilderConfigException("no operation named " + operationName
            + " found on service interface: "
            + serviceInterface.getName());
    }

    public QName getBindingType() {
        return BINDING_WS;
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation, boolean isCallback) throws TargetInvokerCreationException {
        // TODO Auto-generated method stub
        return null;
    }

    public TargetInvoker createTargetInvoker(InterfaceContract contract, Operation operation)
        throws TargetInvokerCreationException {
//        if (!operation.isCallback()) { TODO: no isCallback methjod yet?
//            throw new UnsupportedOperationException();
//        } else {
            return new Axis2ServiceCallbackTargetInvoker(this);
//        }
    }

    public void addMapping(Object msgId, InvocationContext invCtx) {
        this.invCtxMap.put(msgId, invCtx);
    }

    public InvocationContext retrieveMapping(Object msgId) {
        return this.invCtxMap.get(msgId);
    }

    public void removeMapping(Object msgId) {
        this.invCtxMap.remove(msgId);
    }

    /**
     * @param inMC
     * @return
     */
    protected static String getConversationID(MessageContext inMC) {
        String conversationID = null;
        Iterator i = inMC.getEnvelope().getHeader()
            .getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "From"));
        for (; i.hasNext();) {
            Object a = i.next();
            if (a instanceof OMElement) {
                OMElement ao = (OMElement) a;
                for (Iterator rpI =
                    ao.getChildrenWithName(new QName("http://www.w3.org/2005/08/addressing", "ReferenceParameters"));
                     rpI.hasNext();) {
                    OMElement rpE = (OMElement) rpI.next();
                    for (
                        Iterator cidI = rpE.getChildrenWithName(Axis2TargetInvoker.CONVERSATION_ID_REFPARM_QN);
                        cidI.hasNext();) {
                        OMElement cidE = (OMElement) cidI.next();
                        conversationID = cidE.getText();
                    }
                }

            }

        }
        return conversationID;
    }

    protected class InvocationContext {
        public MessageContext inMessageContext;

        public Operation operation;

        public SOAPFactory soapFactory;

        public CountDownLatch doneSignal;

        public InvocationContext(MessageContext messageCtx,
                                 Operation operation,
                                 SOAPFactory soapFactory,
                                 CountDownLatch doneSignal) {
            this.inMessageContext = messageCtx;
            this.operation = operation;
            this.soapFactory = soapFactory;
            this.doneSignal = doneSignal;
        }
    }

    boolean isConversational() {
        return serviceContract.getInterface().isConversational();
    }

}
