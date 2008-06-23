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
package service.generated;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SomeServiceService", targetNamespace = "http://service/", wsdlLocation = "file:/C:/Tuscany/java/sca/itest/wsdlless/src/main/resources/some.wsdl")
public class SomeServiceService extends Service {

    private final static URL SOMESERVICESERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Tuscany/java/sca/itest/wsdlless/src/main/resources/some.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SOMESERVICESERVICE_WSDL_LOCATION = url;
    }

    public SomeServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SomeServiceService() {
        super(SOMESERVICESERVICE_WSDL_LOCATION, new QName("http://service/", "SomeServiceService"));
    }

    /**
     * 
     * @return
     *     returns SomeService
     */
    @WebEndpoint(name = "SomeServicePort")
    public SomeService getSomeServicePort() {
        return (SomeService)super.getPort(new QName("http://service/", "SomeServicePort"), SomeService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SomeService
     */
    @WebEndpoint(name = "SomeServicePort")
    public SomeService getSomeServicePort(WebServiceFeature... features) {
        return (SomeService)super.getPort(new QName("http://service/", "SomeServicePort"), SomeService.class, features);
    }

}
