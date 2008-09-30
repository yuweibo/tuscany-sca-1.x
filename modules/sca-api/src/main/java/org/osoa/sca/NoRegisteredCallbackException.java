/*
 * Permission to copy, display and distribute the Service Component Architecture Specification and/or
 * portions thereof, without modification, in any medium without fee or royalty is hereby granted, provided
 * that you include the following on ALL copies of the Service Component Architecture Specification, or
 * portions thereof, that you make:
 * 1. A link or URL to the Service Component Architecture Specification at this location:
 *    http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 * 2. The full text of the copyright notice as shown in the Service Component Architecture Specification.
 * BEA, Cape Clear, IBM, Interface21, IONA, Oracle, Primeton, Progress Software, Red Hat, Rogue Wave,
 * SAP, Siemens, Software AG., Sun, Sybase, TIBCO (collectively, the "Authors") agree to grant you a
 * royalty-free license, under reasonable, non-discriminatory terms and conditions to patents that they deem
 * necessary to implement the Service Component Architecture Specification.
 * THE Service Component Architecture SPECIFICATION IS PROVIDED "AS IS," AND THE
 * AUTHORS MAKE NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED,
 * REGARDING THIS SPECIFICATION AND THE IMPLEMENTATION OF ITS CONTENTS,
 * INCLUDING, BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, NON-INFRINGEMENT OR TITLE.
 * THE AUTHORS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR RELATING TO ANY USE OR
 * DISTRIBUTION OF THE Service Components Architecture SPECIFICATION.
 * The name and trademarks of the Authors may NOT be used in any manner, including advertising or
 * publicity pertaining to the Service Component Architecture Specification or its contents without specific,   
 */
package org.osoa.sca;

/**
 * Exception thrown to indicate that no callback has been registered
 * when interacting with a service.
 *
 * @version $Rev$ $Date$
 */
public class NoRegisteredCallbackException extends ServiceRuntimeException {
    private static final long serialVersionUID = 3734864942222558406L;

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException() {
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(String message) {
        super(message);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param message passed to ServiceRuntimeException
     * @param cause   passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from ServiceRuntimeException.
     *
     * @param cause passed to ServiceRuntimeException
     * @see ServiceRuntimeException
     */
    public NoRegisteredCallbackException(Throwable cause) {
        super(cause);
    }
}
