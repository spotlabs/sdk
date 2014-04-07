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
import com.spotlabs.messaging.MessageReceiver;

/**
 * * User: dclark
 * Date: 5/7/13
 * Time: 12:03 PM
 * <p/>
 * Copyright 2013 Spot Labs, Inc.
 */
public class ReceiverWrapper implements Parcelable{
    private MessageReceiver mReceiver;

    public static Creator<ReceiverWrapper> CREATOR = new Creator<ReceiverWrapper>() {
        @Override
        public ReceiverWrapper createFromParcel(Parcel source) {
            return new ReceiverWrapper(source.<MessageReceiver>readParcelable(this.getClass().getClassLoader()));
        }

        @Override
        public ReceiverWrapper[] newArray(int size) {
            return new ReceiverWrapper[0];  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public ReceiverWrapper(MessageReceiver receiver){
        mReceiver = receiver;
    }

    public MessageReceiver getReceiver(){
        return mReceiver;
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mReceiver,flags);
    }
}
