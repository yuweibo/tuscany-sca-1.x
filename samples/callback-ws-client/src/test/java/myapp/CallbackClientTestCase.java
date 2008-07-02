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
package myapp;

import org.apache.tuscany.sca.node.SCANode2;
import org.apache.tuscany.sca.node.SCANode2Factory;
import org.apache.tuscany.sca.node.SCANode2Factory.SCAContribution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the callback server is available
 */
public class CallbackClientTestCase {

    private SCANode2 node;

    @Before
	public void startServer() throws Exception {
        try {
            node = SCANode2Factory.newInstance().createSCANode("jar:file:../callback-ws-service/target/sample-callback-ws-service.jar!/callbackws.composite", new SCAContribution("server", "../callback-ws-service/target/sample-callback-ws-service.jar"));
            node.start();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
	}

	@Test
	public void testClient() throws Exception {
		MyClientImpl.main(null);
	}
    
	@After
	public void stopServer() throws Exception {
        node.stop();
	}
}