/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: com/spotlabs/nv/messaging/IMessageService.aidl
 */
package com.spotlabs.nv.messaging;
/**
 *  * User: dclark
 * Date: 5/7/13
 * Time: 11:04 AM
 * 
 * Copyright 2013 Spot Labs, Inc.
 */
public interface IMessageService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.spotlabs.nv.messaging.IMessageService
{
private static final java.lang.String DESCRIPTOR = "com.spotlabs.nv.messaging.IMessageService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.spotlabs.nv.messaging.IMessageService interface,
 * generating a proxy if needed.
 */
public static com.spotlabs.nv.messaging.IMessageService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.spotlabs.nv.messaging.IMessageService))) {
return ((com.spotlabs.nv.messaging.IMessageService)iin);
}
return new com.spotlabs.nv.messaging.IMessageService.Stub.Proxy(obj);
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
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
com.spotlabs.nv.messaging.MessageWrapper _arg0;
if ((0!=data.readInt())) {
_arg0 = com.spotlabs.nv.messaging.MessageWrapper.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.spotlabs.nv.messaging.ReceiverWrapper _arg1;
if ((0!=data.readInt())) {
_arg1 = com.spotlabs.nv.messaging.ReceiverWrapper.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.sendMessage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_queueMessage:
{
data.enforceInterface(DESCRIPTOR);
com.spotlabs.nv.messaging.MessageWrapper _arg0;
if ((0!=data.readInt())) {
_arg0 = com.spotlabs.nv.messaging.MessageWrapper.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.queueMessage(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.spotlabs.nv.messaging.IMessageService
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
@Override public void sendMessage(com.spotlabs.nv.messaging.MessageWrapper message, com.spotlabs.nv.messaging.ReceiverWrapper receiver) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((message!=null)) {
_data.writeInt(1);
message.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((receiver!=null)) {
_data.writeInt(1);
receiver.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void queueMessage(com.spotlabs.nv.messaging.MessageWrapper message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((message!=null)) {
_data.writeInt(1);
message.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_queueMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_queueMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void sendMessage(com.spotlabs.nv.messaging.MessageWrapper message, com.spotlabs.nv.messaging.ReceiverWrapper receiver) throws android.os.RemoteException;
public void queueMessage(com.spotlabs.nv.messaging.MessageWrapper message) throws android.os.RemoteException;
}
