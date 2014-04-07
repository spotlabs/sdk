package com.spotlabs.nv.messaging;

import com.spotlabs.nv.messaging.MessageWrapper;
import com.spotlabs.nv.messaging.ReceiverWrapper;

/**
 *  * User: dclark
 * Date: 5/7/13
 * Time: 11:04 AM
 * 
 * Copyright 2013 Spot Labs, Inc.
 */
interface IMessageService {
    void sendMessage(in MessageWrapper message, in ReceiverWrapper receiver);
    void queueMessage(in MessageWrapper message);
}
