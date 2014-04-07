/*******************************************************************************
 * Copyright 2013 Spot Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.spotlabs.messaging;

import android.os.RemoteException;
import android.util.Log;
import com.spotlabs.nv.messaging.IMessageService;
import com.spotlabs.nv.messaging.MessageWrapper;
import com.spotlabs.nv.messaging.ReceiverWrapper;

/**
 * The Message Service allows for queuing HTTP messages. This ensures that an HTTP message will
 * reach the server eventually regardless of connectivity.
 * 
 * @author asax
 * @author dclark
 */
public final class MessageService {
    private static final String TAG = "NVCore:MessageService";
    private final IMessageService mService;

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_EXCEPTION = 1;
    public static final String RESULT_EXTRA_RESULT = "com.spotlabs.messaging.result.RESULT";
    public static final String RESULT_EXTRA_EXCEPTION = "com.spotlabs.messaging.result.EXCEPTION";

    /**
     * @param service
     */
    public MessageService(IMessageService service) {
        mService = service;
    }

    /**
     * Queues a message for eventual delivery.
     * @param message the message to be sent
     */
    public void queue(Message message) {
        try{
            mService.queueMessage(new MessageWrapper(message));
        }catch(RemoteException e){
            Log.w(TAG,"Error queueing message",e);
        }
    }

    public void send(Message message, MessageReceiver receiver) throws RemoteException{
        mService.sendMessage(new MessageWrapper(message),new ReceiverWrapper(receiver));
    }
}
