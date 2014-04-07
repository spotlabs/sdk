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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.spotlabs.util.parcel.ParcelUtil.*;

/**
 * A message for making standard POST form requests.
 * 
 */
public class FormDataMessage extends Message {
    private static final String TAG = "SPOTLABS:FormDataMessage";

    public static final Parcelable.Creator<FormDataMessage> CREATOR = new Parcelable.Creator<FormDataMessage>() {
        @Override
        public FormDataMessage createFromParcel(Parcel source)
        {
            return new FormDataMessage(source);
        }

        @Override
        public FormDataMessage[] newArray(int size) {
            return new FormDataMessage[size];  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    private final List<NameValuePair> mBody;

    private FormDataMessage(Parcel source) {
        super(source);
        mBody = readNameValueListFromParcel(source);
    }

    public FormDataMessage(URI endpoint, List<NameValuePair> body) {
        super(endpoint);
        mBody = new ArrayList<NameValuePair>(body);
    }


    @Override
    public HttpEntity getEntity() {
        try {
            return new UrlEncodedFormEntity(mBody);
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG,"Error encoding form data",e);
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        writeNameValueListToParcel(dest, mBody);
    }
    
    public static List<NameValuePair> arrayToNameValueList(String name, String[] data) {
        ArrayList<NameValuePair> out = new ArrayList<NameValuePair>();
    	for (int i = 0; i < data.length; i++) {
            out.add(new BasicNameValuePair(name + "[" + i + "]", data[i]));
        }
    	return out;
    }

    
}

