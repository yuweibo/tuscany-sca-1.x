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
package org.apache.tuscany.sca.test.spec.impl;

import org.apache.tuscany.host.embedded.SCARuntime;
import org.apache.tuscany.sca.test.spec.BasicService;
import org.apache.tuscany.sca.test.spec.MathService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.annotations.Service;

@Service(BasicService.class)
public class BasicServiceImpl implements BasicService {

    public int negate(int theInt) {
        return -theInt;
    }

    public int delegateNegate(int theInt) {
        
        SCARuntime.start("BasicService.composite");
        ComponentContext context = SCARuntime.getComponentContext("MathServiceComponent");
        ServiceReference<MathService> service = context.createSelfReference(MathService.class);
        MathService mathService = service.getService();

        return mathService.negate(theInt);
        
    }
}
