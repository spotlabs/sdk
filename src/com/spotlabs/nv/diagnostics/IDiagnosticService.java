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
 * Original file: ./src/com/spotlabs/nv/diagnostics/IDiagnosticService.aidl
 */
package com.spotlabs.nv.diagnostics;
/**
 * Interface to NV platform diagnostics service.
 */
public interface IDiagnosticService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.spotlabs.nv.diagnostics.IDiagnosticService
{
private static final java.lang.String DESCRIPTOR = "com.spotlabs.nv.diagnostics.IDiagnosticService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.spotlabs.nv.diagnostics.IDiagnosticService interface,
 * generating a proxy if needed.
 */
public static com.spotlabs.nv.diagnostics.IDiagnosticService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.spotlabs.nv.diagnostics.IDiagnosticService))) {
return ((com.spotlabs.nv.diagnostics.IDiagnosticService)iin);
}
return new com.spotlabs.nv.diagnostics.IDiagnosticService.Stub.Proxy(obj);
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
case TRANSACTION_showDiagnostics:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.showDiagnostics(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.spotlabs.nv.diagnostics.IDiagnosticService
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
     * Display the diagnostic screen if provided the correct PIN
     *
     * @param pin the PIN code for this device
     * @return <b>true</b> if the correct PIN was provided.
     * */
@Override public boolean showDiagnostics(java.lang.String pin) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pin);
mRemote.transact(Stub.TRANSACTION_showDiagnostics, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_showDiagnostics = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * Display the diagnostic screen if provided the correct PIN
     *
     * @param pin the PIN code for this device
     * @return <b>true</b> if the correct PIN was provided.
     * */
public boolean showDiagnostics(java.lang.String pin) throws android.os.RemoteException;
}
