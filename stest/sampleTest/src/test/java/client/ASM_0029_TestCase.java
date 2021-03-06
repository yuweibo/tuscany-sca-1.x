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
package client;


import test.ASM_0002_Client;
import testClient.TestInvocation;

/**
 * Client for ASM_0029_TestCase, which tests that where a <component/> <property/> 
 * has @many=false that there is only one <value/> child element of the <property/> 
 */
public class ASM_0029_TestCase extends BaseJAXWSTestCase {

 
	/**
	 * <p>
	 * OSOA Line 1104~1106
	 * <p>
	 * many (optional) - whether the property is single-valued (false) or multi-valued (true).
	 * The default is false. In the case of a multi-valued property, it is presented to the
	 * implementation as a Collection of property values.
	 * <p>
	 * OASIS
	 * <p>
	 * ASM50032
	 * If a property is single-valued, the <value/> subelement MUST NOT occur more than once.
	 * <p>
	 * OSOA didn't explicitly say "MUST NOT occur more than once".
	 * <p>
	 * Jira issue:
	 * https://issues.apache.org/jira/browse/TUSCANY-2902
	 *   
	 */
    protected TestConfiguration getTestConfiguration() {
    	TestConfiguration config = new TestConfiguration();
    	config.testName 		= "ASM_0029";
    	config.input 			= "request";
    	config.output 			= "exception";
    	config.composite 		= "Test_ASM_0029.composite";
    	config.testServiceName 	= "TestClient";
    	config.testClass 		= ASM_0002_Client.class;
    	config.serviceInterface = TestInvocation.class;
    	return config;
    }
    
} // end class Test_ASM_0029
