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
import android.os.ResultReceiver;

import static com.spotlabs.messaging.MessageService.*;

/**
 * A callback for receiving the response from a queued message.
 * 
 * @author dclark
 */
public class MessageReceiver implements Parcelable {

    public static final Creator<MessageReceiver> CREATOR = new Creator<MessageReceiver>() {
        @Override
        public MessageReceiver createFromParcel(Parcel source) {
            return new MessageReceiver(source);
        }

        @Override
        public MessageReceiver[] newArray(int size) {
            return new MessageReceiver[size];
        }
    };

    /**
     * 
     * @author dclark
     */
    public interface MessageHandler {
        void onSuccess(Bundle resultData);
        void onException(Exception e);
    }

    private ResultReceiver mReceiver;

    protected MessageReceiver(Parcel in) {
        mReceiver = ResultReceiver.CREATOR.createFromParcel(in);
    }

    public MessageReceiver(Handler handler ,final MessageHandler receiver) {
        mReceiver = new ResultReceiver(handler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                switch (resultCode) {
                    case RESULT_SUCCESS:
                        receiver.onSuccess(resultData);
                        break;
                    case RESULT_EXCEPTION:
                        Exception e = (Exception) resultData.getSerializable(RESULT_EXTRA_EXCEPTION);
                        receiver.onException(e);
                        break;
                }
            }
        };
    }

    public final void sendSuccess(Bundle resultData) {
        mReceiver.send(RESULT_SUCCESS, resultData);
    }

    public final void sendException(Exception e) {
        Bundle result = new Bundle();
        result.putSerializable(RESULT_EXTRA_EXCEPTION, e);
        mReceiver.send(RESULT_EXCEPTION, result);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mReceiver.writeToParcel(dest, flags);
    }
}
