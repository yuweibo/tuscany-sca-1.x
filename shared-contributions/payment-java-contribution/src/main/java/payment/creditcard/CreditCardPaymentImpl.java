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

package payment.creditcard;

import javax.annotation.security.RolesAllowed;

import org.osoa.sca.annotations.Requires;
import org.osoa.sca.annotations.Service;

@Service(CreditCardPayment.class)
@Requires("{http://www.osoa.org/xmlns/sca/1.0}authorization")
@RolesAllowed("Admin")
public class CreditCardPaymentImpl implements CreditCardPayment {

    public String authorize(CreditCardDetailsType creditCard, float amount) throws AuthorizeFault_Exception {
        if (creditCard != null) {
            System.out.println("Checking card: name = " + creditCard.getCardOwner().getName()
                + " number = "
                + creditCard.getCreditCardNumber()
                + " for amount "
                + amount);
        } else {
            System.out.println("Checking card is null");
            ObjectFactory factory = new ObjectFactory();
            AuthorizeFault fault = factory.createAuthorizeFault();
            fault.setErrorCode("001 - Invalid card");
            AuthorizeFault_Exception ex = new AuthorizeFault_Exception("Invalid card", fault);
            throw ex;
        }

        return "OK";
    }
}
