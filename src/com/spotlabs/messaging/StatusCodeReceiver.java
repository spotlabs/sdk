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

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import static com.spotlabs.messaging.MessageService.RESULT_EXTRA_RESULT;

/**
 * @hide
 */
public final class StatusCodeReceiver extends MessageReceiver {

    public static final Parcelable.Creator<StatusCodeReceiver> CREATOR = new Parcelable.Creator<StatusCodeReceiver>() {
        @Override
        public StatusCodeReceiver createFromParcel(Parcel source) {
            return new StatusCodeReceiver(source);
        }

        @Override
        public StatusCodeReceiver[] newArray(int size) {
            return new StatusCodeReceiver[size];
        }
    };

    public interface StatusCodeHandler{
        void onSuccess(int statusCode);
        void onException(Exception e);
    }

    public StatusCodeReceiver(Parcel source){
        super(source);
    }

    public StatusCodeReceiver(Handler handler, final StatusCodeHandler statusCodeHandler){
        super(handler, new MessageHandler() {
            @Override
            public void onSuccess(Bundle resultData) {
                statusCodeHandler.onSuccess(resultData.getInt(RESULT_EXTRA_RESULT));
            }

            @Override
            public void onException(Exception e) {
                statusCodeHandler.onException(e);
            }
        });
    }
}
