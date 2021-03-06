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
package org.apache.tuscany.sca.contribution.service.impl;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.sca.contribution.PackageType;
import org.apache.tuscany.sca.contribution.service.impl.PackageTypeDescriberImpl;

public class PackageTypeDescriberImplTestCase extends TestCase {
    private PackageTypeDescriberImpl packageTypeDescriber;

    public void testResolveArchivePackageType() throws Exception {
        URL artifactURL = getClass().getResource("/deployables/sample-calculator.jar");
        assertEquals(PackageType.JAR, this.packageTypeDescriber.getType(artifactURL, null));
    }

    public void testResolveFolderPackageType() throws Exception {
        URL artifactURL = getClass().getResource("/deployables/");
        assertEquals(PackageType.FOLDER, this.packageTypeDescriber.getType(artifactURL, null));
    }

    public void testResolveFolder2PackageType() throws Exception {
        URL artifactURL = getClass().getResource("/deployables");
        assertEquals(PackageType.FOLDER, this.packageTypeDescriber.getType(artifactURL, null));
    }

    
    public void testResolveUnknownPackageType() throws Exception {
        URL artifactURL = getClass().getResource("/test.ext");
        assertNull(this.packageTypeDescriber.getType(artifactURL, null));
    }
    
    public void testDefaultPackageType() throws Exception {
        URL artifactURL = getClass().getResource("/test.ext");
        assertEquals("application/vnd.tuscany.ext", 
                packageTypeDescriber.getType(artifactURL, "application/vnd.tuscany.ext"));        
    }

    @Override
    protected void setUp() throws Exception {
        packageTypeDescriber = new PackageTypeDescriberImpl();
    }

}
