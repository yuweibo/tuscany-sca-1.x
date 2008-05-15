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

package org.apache.tuscany.sca.runtime.tomcat;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.tuscany.sca.host.webapp.WebAppServletHost;

/**
 * A Servlet that forwards requests to the Servlets registered with the Tuscany
 * ServletHost.
 * 
 * TODO: Copy of host-webapp TuscanyServlet. Can be removed once host-webapp TuscanyServlet
 *       moved over to latest SCADomain API.
 *
 * @version $Rev$ $Date$
 */
public class TuscanyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private transient WebAppServletHost servletHost;

    @Override
    public void init(ServletConfig config) throws ServletException {
        servletHost = WebAppServletHost.getInstance();
        servletHost.init(config);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String path = ((HttpServletRequest)req).getPathInfo();
        RequestDispatcher dispatcher = servletHost.getRequestDispatcher(path);
        if (dispatcher == null) {
            throw new IllegalStateException("No servlet registered for path: " + path);
        }

        dispatcher.forward(req, res);
    }

}
