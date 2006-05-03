/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.axis2.builder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import commonj.sdo.helper.TypeHelper;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContextConstants;
import org.apache.axis2.description.AxisService;
import org.apache.tuscany.binding.axis2.config.WSExternalServiceContextFactory;
import org.apache.tuscany.binding.axis2.handler.Axis2OperationInvoker;
import org.apache.tuscany.binding.axis2.handler.Axis2ServiceInvoker;
import org.apache.tuscany.binding.axis2.handler.WebServicePortMetaData;
import org.apache.tuscany.binding.axis2.util.DataBinding;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.extension.ExternalServiceContextFactory;
import org.apache.tuscany.core.extension.ExternalServiceBuilderSupport;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.ws.commons.om.OMAbstractFactory;
import org.apache.ws.commons.soap.SOAPFactory;
import org.osoa.sca.annotations.Scope;

/**
 * Creates a <code>ContextFactory</code> for an external service configured with the {@link
 * WebServiceBinding}
 */
@Scope("MODULE")
public class ExternalWebServiceBuilder extends ExternalServiceBuilderSupport<WebServiceBinding> {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.tuscany.core.extension.ExternalServiceBuilderSupport#createExternalServiceContextFactory(org.apache.tuscany.model.assembly.ExternalService)
     */
    @Override
    protected ExternalServiceContextFactory createExternalServiceContextFactory(ExternalService externalService) {

        WebServiceBinding wsBinding = (WebServiceBinding) externalService.getBindings().get(0);
        Definition wsdlDefinition = wsBinding.getWSDLDefinition();
        WebServicePortMetaData wsPortMetaData = new WebServicePortMetaData(wsdlDefinition, wsBinding.getWSDLPort(), wsBinding.getURI(), false);

        ServiceClient serviceClient = createServiceClient(externalService.getName(), wsdlDefinition, wsPortMetaData);

        TypeHelper typeHelper = wsBinding.getTypeHelper();
        Class serviceInterface = externalService.getConfiguredService().getPort().getServiceContract().getInterface();
        Map<String, Axis2OperationInvoker> invokers = createOperationInvokers(serviceInterface, typeHelper, wsPortMetaData);

        Axis2ServiceInvoker axis2Client = new Axis2ServiceInvoker(serviceClient, invokers);

        return new WSExternalServiceContextFactory(externalService.getName(), new SingletonObjectFactory<Axis2ServiceInvoker>(axis2Client));

    }

    /**
     * Create an Axis2 ServiceClient configured for the externalService
     */
    protected ServiceClient createServiceClient(String externalServiceName, Definition wsdlDefinition, WebServicePortMetaData wsPortMetaData) {

        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator(null, null);
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();

        QName serviceQName = wsPortMetaData.getServiceName();
        String portName = wsPortMetaData.getPortName().getLocalPart();

        ServiceClient serviceClient;
        try {

            AxisService axisService = AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());
            serviceClient = new ServiceClient(configurationContext, axisService);

        } catch (AxisFault e) {
            BuilderConfigException bce = new BuilderConfigException("AxisFault creating external service", e);
            bce.addContextName(externalServiceName);
            throw bce;
        }

        return serviceClient;
    }

    /**
     * Create and configure an Axis2OperationInvoker for each operation in the externalService
     */
    protected Map<String, Axis2OperationInvoker> createOperationInvokers(Class sc, TypeHelper typeHelper, WebServicePortMetaData wsPortMetaData) {
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        String portTypeNS = wsPortMetaData.getPortTypeName().getNamespaceURI();
        Map<String, Axis2OperationInvoker> invokers = new HashMap<String, Axis2OperationInvoker>();

        for (Method m : sc.getMethods()) {
            String methodName = m.getName();
            QName wsdlOperationName = new QName(portTypeNS, wsPortMetaData.getWSDLOperationName(methodName));
            DataBinding dataBinding = new SDODataBinding(typeHelper, wsdlOperationName);
            Options options = new Options();
            options.setTo(new EndpointReference(wsPortMetaData.getEndpoint()));
            options.setProperty(MessageContextConstants.CHUNKED, Boolean.FALSE); // TODO: don't do this for wrapped style
            Axis2OperationInvoker invoker = new Axis2OperationInvoker(wsdlOperationName, options, dataBinding, soapFactory);
            invokers.put(methodName, invoker);
        }

        return invokers;
    }

}
