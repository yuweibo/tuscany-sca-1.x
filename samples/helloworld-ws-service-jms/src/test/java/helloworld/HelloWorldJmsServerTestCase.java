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
package helloworld;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests that the helloworld server is available
 */
public class HelloWorldJmsServerTestCase{

    private SCADomain scaDomain;

        @Before
	public void startServer() throws Exception {
		scaDomain = SCADomain.newInstance("helloworldwsjms.composite");
	}

    
    @Test
    public void testServiceCall() throws IOException {
        HelloWorldService helloWorldService = scaDomain.getService(HelloWorldService.class, "HelloWorldServiceComponent/HelloWorldService");
        assertNotNull(helloWorldService);
        
        assertEquals("Hello Smith", helloWorldService.getGreetings("Smith"));
    }

	@After
	public void stopServer() throws Exception {
            if (scaDomain != null) {
                scaDomain.close();
            }
	}

}
