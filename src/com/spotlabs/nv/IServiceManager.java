/*
 * Copyright 2014 Spot Labs Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: ./src/com/spotlabs/nv/IServiceManager.aidl
 */
package com.spotlabs.nv;
/**
 * Interface to NV platform service manager.
 */
public interface IServiceManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.spotlabs.nv.IServiceManager
{
private static final java.lang.String DESCRIPTOR = "com.spotlabs.nv.IServiceManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.spotlabs.nv.IServiceManager interface,
 * generating a proxy if needed.
 */
public static com.spotlabs.nv.IServiceManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.spotlabs.nv.IServiceManager))) {
return ((com.spotlabs.nv.IServiceManager)iin);
}
return new com.spotlabs.nv.IServiceManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.IBinder _result = this.getService(_arg0);
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
case TRANSACTION_registerService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.IBinder _arg1;
_arg1 = data.readStrongBinder();
this.registerService(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.spotlabs.nv.IServiceManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Get a remote interface to an NV service.
     * @param serviceName The name of the service
     * @return A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
@Override public android.os.IBinder getService(java.lang.String serviceName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(serviceName);
mRemote.transact(Stub.TRANSACTION_getService, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
     * Register a remote service with the service manager.
     * @param serviceName The name of the service.
     * @param service A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
@Override public void registerService(java.lang.String serviceName, android.os.IBinder service) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(serviceName);
_data.writeStrongBinder(service);
mRemote.transact(Stub.TRANSACTION_registerService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
     * Get a remote interface to an NV service.
     * @param serviceName The name of the service
     * @return A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
public android.os.IBinder getService(java.lang.String serviceName) throws android.os.RemoteException;
/**
     * Register a remote service with the service manager.
     * @param serviceName The name of the service.
     * @param service A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
public void registerService(java.lang.String serviceName, android.os.IBinder service) throws android.os.RemoteException;
}
