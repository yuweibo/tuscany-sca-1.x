/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.spi.loader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.model.ModelObject;

/**
 * A loader that creates a model object from a StAX input stream.
 *
 * @version $Rev$ $Date$
 */
public interface StAXElementLoader<T extends ModelObject> {
    /**
     * Build the model object for an element in an XML stream.
     * When this method returns the stream will be positioned on the corresponding END_ELEMENT.
     *
     * @param reader the XML stream reader positioned on the applicable START_ELEMENT
     * @param loaderContext the context for the load operation
     * @return the model object for that element
     */
    T load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, LoaderException;
}
