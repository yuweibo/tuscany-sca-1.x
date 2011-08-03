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
package org.apache.tuscany.sca.implementation.spring.invocation;

public class DefaultConfigurationPropertiesExtensionPoint implements ConfigurationPropertiesExtensionPoint {
	
	// Contructor
	public DefaultConfigurationPropertiesExtensionPoint() {
		
	}
	
	// By default SCA annotations for implementation.spring
	// will be supproted.
	public boolean isAnnotationSupported() {
		return true;
	}
	
	// By defauly all the Spring version are supported for 
	// Tuscany.
	public String getSupportedVersion() {
		
		// Customize the code here for a limited version
		// support
		/* 
		return "2.5.5"; 
		*/		
		return "ANY";
	}
}