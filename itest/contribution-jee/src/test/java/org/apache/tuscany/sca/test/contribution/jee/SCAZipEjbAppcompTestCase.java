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

package org.apache.tuscany.sca.test.contribution.jee;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.jee.ExternalEarInfo;
import org.apache.tuscany.sca.contribution.jee.JavaEEApplicationInfo;
import org.apache.tuscany.sca.contribution.jee.JavaEEIntrospector;
import org.apache.tuscany.sca.contribution.jee.impl.JavaEEApplicationInfoImpl;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.implementation.ejb.EJBImplementation;
import org.apache.tuscany.sca.implementation.jee.JEEImplementation;
import org.apache.tuscany.sca.implementation.web.WebImplementation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class SCAZipEjbAppcompTestCase {
 
    private static final String CONTRIBUTION_001_ID = "contribution001/";

    private ClassLoader cl;
    private EmbeddedSCADomain domain;
    private ContributionService contributionService;

    @Before
    public void setUp() throws Exception {
        //Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        //get a reference to the contribution service
        contributionService = domain.getContributionService();
    }

    /**
     * SCAZIP      - It's an SCA contribution in a ZIP with no nested archive
     *               but with a reference to an EJB Jar
     * JAR
     *    appcomp  - It has an application composite in it
     *    
     */
    @Test
    public void testSCAZipEjbAppcomp() throws Exception {

        URL contributionLocation = new File("../contribution-jee-samples/scazip-ejb-appcomp/target/itest-contribution-jee-samples-10-scazip-ejb-appcomp.zip").toURL();
        Contribution contribution =  contributionService.contribute(CONTRIBUTION_001_ID, contributionLocation, false);
        
        Assert.assertNotNull(contribution);
        
        Composite composite = null;
        for (Artifact artifact : contribution.getArtifacts()){
            if (artifact.getModel() instanceof Composite){
                composite = (Composite) artifact.getModel();
            }
        }
        
        Assert.assertNotNull(composite);
        
        Assert.assertEquals(2, composite.getComponents().size());
        Assert.assertEquals(1, composite.getComponents().get(1).getImplementation().getServices().size());            
        Assert.assertEquals("HelloworldServiceBean_HelloworldService", composite.getComponents().get(1).getImplementation().getServices().get(0).getName());
        
        domain.buildComposite(composite);
        
        // assert that the build process has worked
        Assert.assertNotNull(composite);

    }

}
