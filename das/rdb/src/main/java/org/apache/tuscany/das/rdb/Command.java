/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.das.rdb;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * A Command is used to execute a read or write to a database
 * 
 * 
 */
public interface Command {

//	public static CommandFactory FACTORY = new CommandFactoryImpl();

	/**
	 * Performs the function defined by the command
	 */
	public void execute();

	/**
	 * Performs the function defined by the command and return the results in
	 * the root DataObject
	 * 
	 * @return the root DataObject
	 */
	public DataObject executeQuery();

	/**
	 * Sets the value of the named Parameter
	 * 
	 * @param name
	 *            the name of this Parameter
	 * @param value
	 *            the value for the Parameter
	 */
	public void setParameterValue(String name, Object value);

	/**
	 * Sets the value of the associated Parameter
	 * 
	 * @param index
	 *            the index of the Parameter
	 * @param value
	 *            the value for the Parameter
	 */
	public void setParameterValue(int index, Object value);

    /**
     * Sets the "type" of the associated Parameter
     * 
     * @param name
     *            the name of this Parameter
     * @param type
     *            the SDODataTypes-defined "type" for the Parameter.
     */
    public void setParameterType(String string, Type dataType);
     
     /**
	 * Returns the value of the associated Parameter
	 * 
	 * @param name
	 *            the name of the Parameter
	 * @return the value of the Parameter
	 */
	public Object getParameterValue(String name);

	/**
	 * Returns the value of the associated Parameter
	 * 
	 * @param index
	 *            the index of the Parameter
	 * @return the value of the Parameter
	 */
	public Object getParameterValue(int index);


	/**
	 * Provides the java.sql.Connection to be used for this executing this
	 * command.
	 * 
	 * @param connection
	 *            the java.sql.Connection
	 */
//	public void setConnection(Connection connection);

	/**
	 * Provides the java.sql.Connection to be used for this executing this
	 * command.
	 * 
	 * @param connection
	 *            the java.sql.Connection
	 * @param manageTransactions
	 *            <code>true</code> if the DAS should perform tx
	 *            commit/rollback
	 */
//	public void setConnection(Connection connection, boolean manageTransactions);

	/**
	 * Cleans up and realeases all resources associated with this command. This
	 * should be called when the application is done with this command.
	 */
	public void close();
	

}
