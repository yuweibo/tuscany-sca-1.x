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
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.OutputStream;
import java.util.Map;

/**
 * This is the Java2WSDL Generator facade that will be used by Tuscany
 * components for java to wsdl conversion.
 * 
 */
public interface Java2WSDLGenerator {
	public void generateWSDL(String[] args);

	public void generateWSDL(Map commandLineOptions);

	public void addWSDLGenListener(WSDLGenListener l);

	public void removeWSDLGenListener(WSDLGenListener l);

	public Map getCommandLineOptions();

	public void setCommandLineOptoins(Map cmdLineOpts);

	public WSDLModel getWSDLModel();

	public OutputStream getOutputStream();

	public void setOutputStream(OutputStream outStream);

}
