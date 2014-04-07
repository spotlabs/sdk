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
package com.spotlabs.nv.messaging;

import android.os.Parcel;
import android.os.Parcelable;
import com.spotlabs.messaging.Message;

/**
 * * User: dclark
 * Date: 5/7/13
 * Time: 11:57 AM
 * <p/>
 * Copyright 2013 Spot Labs, Inc.
 */

public class MessageWrapper implements Parcelable{

    private Message mMessage;

    public static Creator<MessageWrapper> CREATOR = new Creator<MessageWrapper>() {
        @Override
        public MessageWrapper createFromParcel(Parcel source) {
            return new MessageWrapper(source.<Message>readParcelable(this.getClass().getClassLoader()));
        }

        @Override
        public MessageWrapper[] newArray(int size) {
            return new MessageWrapper[size];  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public MessageWrapper(Message message){
        mMessage = message;
    }

    public Message getMessage(){
        return mMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mMessage,flags);
    }
}
