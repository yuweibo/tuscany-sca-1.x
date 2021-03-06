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

package org.apache.tuscany.sca.node.launcher;

import static org.apache.tuscany.sca.node.launcher.NodeLauncherUtil.node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for SCA nodes.
 *  
 * @version $Rev$ $Date$
 */
public class NodeLauncher {

    static final Logger logger = Logger.getLogger(NodeLauncher.class.getName());

    /**
     * Constructs a new node launcher.
     */
    private NodeLauncher() {
    }
    
    /**
     * Returns a new launcher instance.
     *  
     * @return a new launcher instance
     */
    public static NodeLauncher newInstance() {
        return new NodeLauncher();
    }

    /**
     * Creates a new SCA node from the configuration URL
     * 
     * @param configurationURL the URL of the node configuration which is the ATOM feed
     * that contains the URI of the composite and a collection of URLs for the contributions
     *  
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNodeFromURL(String configurationURL) throws LauncherException {
        return (T)node(configurationURL, null, null, null, null);
    }
    
    /**
     * Creates a new SCA Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related 
     * artifacts. If the list is empty, then we will use the thread context classloader to discover
     * the contribution on the classpath
     *   
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, null, contributions, null);
    }
    
    /**
     * Creates a new SCA Node.
     * 
     * @param compositeURI the URI of the composite to use 
     * @param compositeContent the XML content of the composite to use 
     * @param contributions the URI of the contributions that provides the composites and related artifacts 
     * @return a new SCA node.
     * @throws LauncherException
     */
    public <T> T createNode(String compositeURI, String compositeContent, Contribution...contributions) throws LauncherException {
        return (T)node(null, compositeURI, compositeContent, contributions, null);
    }
    
    /**
     * Create a SCA node based on the discovery of the contribution on the classpath for the 
     * given classloader. This method should be treated a convenient shortcut with the following
     * assumptions:
     * <ul>
     * <li>This is a standalone application and there is a deployable composite file on the classpath.
     * <li>There is only one contribution which contains the deployable composite file physically in its packaging hierarchy.
     * </ul> 
     * 
     * @param compositeURI The URI of the composite file relative to the root of the enclosing contribution
     * @param classLoader The ClassLoader used to load the composite file as a resource. If the value is null,
     * then thread context classloader will be used
     * @return A newly created SCA node
     */
    public <T> T createNodeFromClassLoader(String compositeURI, ClassLoader classLoader) throws LauncherException {
        return (T)node(null, compositeURI, null, null, classLoader);
    }
    
    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Node is starting...");

        // Create a node launcher
        NodeLauncher launcher = newInstance();

        Object node = null;
        ShutdownThread shutdown = null;
        try {
            while (true) {
                if (args.length ==1) {
                    
                    // Create a node from a configuration URI
                    String configurationURI = args[0];
                    logger.info("SCA Node configuration: " + configurationURI);
                    node = launcher.createNodeFromURL(configurationURI);
                } else {

                    // Create a node from a composite URI and a contribution location
                    String compositeURI = args[0];
                    if ("-".equals(compositeURI)) {
                        compositeURI = null;
                    } else {
                        logger.info("SCA composite: " + compositeURI);
                    }
                    Contribution contributions[] = new Contribution[args.length - 1];
                    for (int i = 1; i < args.length; i++) {
                        String contributionLocation = args[i];
                        logger.info("SCA contribution: " + contributionLocation);
                        contributions[i - 1] = new Contribution("contribution" + (i - 1), contributionLocation);
                    }
                    node = launcher.createNode(compositeURI, contributions);
                }
                
                // Start the node
                try {
                    node.getClass().getMethod("start").invoke(node);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "SCA Node could not be started", e);
                    throw e;
                }
                logger.info("SCA Node is now started.");
                
                // Install a shutdown hook
                shutdown = new ShutdownThread(node);
                Runtime.getRuntime().addShutdownHook(shutdown);
                
                logger.info("Press 'q' to quit, 'r' to restart.");
                int k = 0;
                try {
                    while ((k != 'q') && (k != 'r')) {
                        k = System.in.read();
                    }
                } catch (IOException e) {
                    
                    // Wait forever
                    Object lock = new Object();
                    synchronized(lock) {
                        lock.wait();
                    }
                }
                
                // Stop the node
                if (node != null) {
                    Object n = node;
                    node = null;
                    stopNode(n);
                }
                
                // Quit
                if (k == 'q' ) {
                    break;
                }
            }
        } catch (Exception e) {
            // Stop the node
            if (node != null) {
                try {
                    Object n = node;
                    node = null;
                    stopNode(n);
                } catch (Exception e2) {
                }
            }
            throw e;
            
        } finally {

            // Remove the shutdown hook
            if (shutdown != null) {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            }
        }
    }

    /**
     * Stop the given node.
     * 
     * @param node
     * @throws Exception
     */
    private static void stopNode(Object node) throws Exception {
        try {
            node.getClass().getMethod("stop").invoke(node);
            logger.info("SCA Node is now stopped.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node could not be stopped", e);
            throw e;
        }
    }
    
    private static class ShutdownThread extends Thread {
        private Object node;

        public ShutdownThread(Object node) {
            super();
            this.node = node;
        }

        public void run() {
            try {
                stopNode(node);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
