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
import org.junit.Test;


/**
 * test if @wiredByImpl="true", 
 * No <wire/> elements have the <reference/> element declared as the @source for the wire.
 */
// @Ignore("TUSCANY-2925")
public class ASM_0042_TestCase extends BaseJAXWSTestCase {

 
	/**
	 * <p>
	 * OSOA
	 * Line 220~221
	 * If "true" is set, then the reference should not be wired 
	 * statically within a composite, but left unwired.
	 * <p>
	 * OASIS
	 * ASM50013
	 * If @wiredByImpl="true", 
	 * other methods of specifying the target service MUST NOT be used.
	 * <p>
	 * Jira issue:
	 * https://issues.apache.org/jira/browse/TUSCANY-2913
	 */
    protected TestConfiguration getTestConfiguration() {
    	TestConfiguration config = new TestConfiguration();
    	config.testName 		= "ASM_0042";
    	config.input 			= "request";
    	config.output 			= "exception";
    	config.composite 		= "Test_ASM_0042.composite";
    	config.testServiceName 	= "TestClient";
    	config.testClass 		= ASM_0002_Client.class;
    	config.serviceInterface = TestInvocation.class;
    	return config;
    }
    
} // end class Test_ASM_0042
