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

package org.apache.tuscany.sca.binding.corba.testing.generated;


/**
* Tester/_PrimitivesSetterImplBase.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tester.idl
* pi�tek, 30 maj 2008 17:04:42 CEST
*/

public abstract class _PrimitivesSetterImplBase extends org.omg.CORBA.portable.ObjectImpl
                implements org.apache.tuscany.sca.binding.corba.testing.generated.PrimitivesSetter, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors
  public _PrimitivesSetterImplBase ()
  {
  }

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("setBoolean", new java.lang.Integer (0));
    _methods.put ("setChar", new java.lang.Integer (1));
    _methods.put ("setWchar", new java.lang.Integer (2));
    _methods.put ("setOctet", new java.lang.Integer (3));
    _methods.put ("setShort", new java.lang.Integer (4));
    _methods.put ("setUnsignedShort", new java.lang.Integer (5));
    _methods.put ("setLong", new java.lang.Integer (6));
    _methods.put ("setUnsignedLong", new java.lang.Integer (7));
    _methods.put ("setLongLong", new java.lang.Integer (8));
    _methods.put ("setUnsignedLongLong", new java.lang.Integer (9));
    _methods.put ("setFloat", new java.lang.Integer (10));
    _methods.put ("setDouble", new java.lang.Integer (11));
    _methods.put ("setString", new java.lang.Integer (12));
    _methods.put ("setWstring", new java.lang.Integer (13));
    _methods.put ("setRemoteObject", new java.lang.Integer (14));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // Tester/PrimitivesSetter/setBoolean
       {
         boolean arg = in.read_boolean ();
         boolean $result = false;
         $result = this.setBoolean (arg);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // Tester/PrimitivesSetter/setChar
       {
         char arg = in.read_char ();
         char $result = (char)0;
         $result = this.setChar (arg);
         out = $rh.createReply();
         out.write_char ($result);
         break;
       }

       case 2:  // Tester/PrimitivesSetter/setWchar
       {
         char arg = in.read_wchar ();
         char $result = (char)0;
         $result = this.setWchar (arg);
         out = $rh.createReply();
         out.write_wchar ($result);
         break;
       }

       case 3:  // Tester/PrimitivesSetter/setOctet
       {
         byte arg = in.read_octet ();
         byte $result = (byte)0;
         $result = this.setOctet (arg);
         out = $rh.createReply();
         out.write_octet ($result);
         break;
       }

       case 4:  // Tester/PrimitivesSetter/setShort
       {
         short arg = in.read_short ();
         short $result = (short)0;
         $result = this.setShort (arg);
         out = $rh.createReply();
         out.write_short ($result);
         break;
       }

       case 5:  // Tester/PrimitivesSetter/setUnsignedShort
       {
         short arg = in.read_ushort ();
         short $result = (short)0;
         $result = this.setUnsignedShort (arg);
         out = $rh.createReply();
         out.write_ushort ($result);
         break;
       }

       case 6:  // Tester/PrimitivesSetter/setLong
       {
         int arg = in.read_long ();
         int $result = (int)0;
         $result = this.setLong (arg);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 7:  // Tester/PrimitivesSetter/setUnsignedLong
       {
         int arg = in.read_ulong ();
         int $result = (int)0;
         $result = this.setUnsignedLong (arg);
         out = $rh.createReply();
         out.write_ulong ($result);
         break;
       }

       case 8:  // Tester/PrimitivesSetter/setLongLong
       {
         long arg = in.read_longlong ();
         long $result = (long)0;
         $result = this.setLongLong (arg);
         out = $rh.createReply();
         out.write_longlong ($result);
         break;
       }

       case 9:  // Tester/PrimitivesSetter/setUnsignedLongLong
       {
         long arg = in.read_ulonglong ();
         long $result = (long)0;
         $result = this.setUnsignedLongLong (arg);
         out = $rh.createReply();
         out.write_ulonglong ($result);
         break;
       }

       case 10:  // Tester/PrimitivesSetter/setFloat
       {
         float arg = in.read_float ();
         float $result = (float)0;
         $result = this.setFloat (arg);
         out = $rh.createReply();
         out.write_float ($result);
         break;
       }

       case 11:  // Tester/PrimitivesSetter/setDouble
       {
         double arg = in.read_double ();
         double $result = (double)0;
         $result = this.setDouble (arg);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 12:  // Tester/PrimitivesSetter/setString
       {
         String arg = in.read_string ();
         String $result = null;
         $result = this.setString (arg);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 13:  // Tester/PrimitivesSetter/setWstring
       {
         String arg = in.read_wstring ();
         String $result = null;
         $result = this.setWstring (arg);
         out = $rh.createReply();
         out.write_wstring ($result);
         break;
       }

       case 14:  // Tester/PrimitivesSetter/setRemoteObject
       {
         org.apache.tuscany.sca.binding.corba.testing.generated.RemoteObject obj = org.apache.tuscany.sca.binding.corba.testing.generated.RemoteObjectHelper.read (in);
         org.apache.tuscany.sca.binding.corba.testing.generated.RemoteObject $result = null;
         $result = this.setRemoteObject (obj);
         out = $rh.createReply();
         org.apache.tuscany.sca.binding.corba.testing.generated.RemoteObjectHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:Tester/PrimitivesSetter:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


} // class _PrimitivesSetterImplBase