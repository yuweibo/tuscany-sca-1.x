/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.core.loader.assembly;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.types.wsdl.WSDLServiceContract;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @version $Rev$ $Date$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class InterfaceWSDLLoader extends AbstractLoader {
    public QName getXMLType() {
        return AssemblyConstants.INTERFACE_WSDL;
    }

    public Class<WSDLServiceContract> getModelType() {
        return WSDLServiceContract.class;
    }

    public WSDLServiceContract load(XMLStreamReader reader, ResourceLoader resourceLoader) throws XMLStreamException, ConfigurationLoadException {
        assert AssemblyConstants.INTERFACE_WSDL.equals(reader.getName());
        WSDLServiceContract serviceContract = factory.createWSDLServiceContract();
        serviceContract.setScope(Scope.INSTANCE);
        serviceContract.setPortTypeURI(reader.getAttributeValue(null, "interface"));
        serviceContract.setCallbackPortTypeURI(reader.getAttributeValue(null, "callbackInterface"));
        return serviceContract;
    }
}
