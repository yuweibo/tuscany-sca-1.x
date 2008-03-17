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

package org.apache.tuscany.sca.implementation.node.launcher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A launcher for the SCA Node daemon.
 *  
 * @version $Rev$ $Date$
 */
public class NodeImplementationDaemon {

    private final static Logger logger = Logger.getLogger(NodeImplementationDaemon.class.getName());
    
    public static void main(String[] args) throws Exception {
        logger.info("Apache Tuscany SCA Node Daemon starting...");

        Class<?> daemonClass;
        Object daemon;
        try {
            // Set up runtime classloader
            ClassLoader runtimeClassLoader = NodeLauncherUtil.runtimeClassLoader();
            Thread.currentThread().setContextClassLoader(runtimeClassLoader);

            // Create the daemon
            
            // We use Java reflection here as only the runtime class
            // loader knows the runtime classes required by the daemon
            daemonClass = runtimeClassLoader.loadClass("org.apache.tuscany.sca.implementation.node.launcher.NodeImplementationDaemonBootstrap"); 
            daemon = daemonClass.getConstructor().newInstance();
            
            // Start the daemon
            daemonClass.getMethod("start").invoke(daemon);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node Daemon could not be started", e);
            throw e;
        }
        
        logger.info("SCA Node Daemon started.");
        logger.info("Press enter to shutdown.");
        try {
            System.in.read();
        } catch (IOException e) {}

        // Stop the daemon
        try {
            daemonClass.getMethod("stop").invoke(daemon);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SCA Node Daemon could not be stopped", e);
            throw e;
        }
    }

}
