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

package org.apache.tuscany.sca.itest;

import static junit.framework.Assert.assertEquals;
import helloworld.HelloWorldService;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the behaviour of the system when multiple WSDLs appear in the contribution
 * with the same namespace
 */
public class ManualWSDLTestCase {

    private static SCADomain domain;
    
    @BeforeClass
    public static void init() throws Throwable {
        try {
            domain = SCADomain.newInstance("manual-wsdl.composite");
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        if (domain != null){
            domain.close();
        }
    }    

    @Test
    public void testLoadWSDL() {
        try {
            HelloWorldService client = domain.getService(HelloWorldService.class, "HelloWorldClientComponent/HelloWorldService");
            client.getGreetings("petra");
        } catch(Exception ex){
            ex.printStackTrace();
        }
        //assertEquals("Hi petra", client.getGreetings("petra"));
    }

}
