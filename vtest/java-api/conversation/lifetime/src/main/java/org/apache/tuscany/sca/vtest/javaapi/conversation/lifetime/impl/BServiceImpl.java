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

package org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.impl;

import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.AServiceCallback;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.BService;
import org.apache.tuscany.sca.vtest.javaapi.conversation.lifetime.BServiceBusinessException;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.ConversationAttributes;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Service(BService.class)
@Scope("CONVERSATION")
@ConversationAttributes(maxAge="1 seconds")
public class BServiceImpl implements BService {

    String someState;

    @Callback
    protected AServiceCallback callback;

    public void setState(String someState) {
        this.someState = someState;
    }

    public String getState() {
        return someState;
    }

    public void endConversation() {
        System.out.println("Someone called Bservice.endsConversation()");
    }

    public void endConversationViaCallback() {
        callback.endConversation();
    }

    public void throwNonBusinessException() {
        throw new Error();
    }
    
    public void throwBusinessException() throws BServiceBusinessException {
        throw new BServiceBusinessException("Business Exception");
    }

}
