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
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.spotlabs.util.parcel.ParcelUtil.*;

/**
 * An HTTP message to be used with the message service. This contains the url, body and headers
 * for the request.
 * 
 * @author dclark
 */
public class Message implements Parcelable {

    protected final URI mEndPoint;
    protected final List<NameValuePair> mHeaders;

    protected Message(Parcel source) {
        mEndPoint = URI.create(source.readString());
        mHeaders = readNameValueListFromParcel(source);
    }

    /**
     * @param endpoint The URL of the message to be sent
     */
    public Message(URI endpoint) {
        mEndPoint = endpoint;
        mHeaders = new ArrayList<NameValuePair>();
    }

    /**
     * 
     * @param endpoint The URL of the message to be sent
     * @param headers Additional headers to be sent up with the request
     */
    protected Message(URI endpoint, Collection<NameValuePair> headers) {
        mEndPoint = endpoint;
        mHeaders = new ArrayList<NameValuePair>(headers);
    }

    /**
     * @return the URL of the message to be sent
     */
    public URI getEndPoint() {
        return mEndPoint;
    }

    /**
     * 
     * @return Additional headers to be sent up with the request
     */
    public Iterable<NameValuePair> getHeaders() {
        return mHeaders;
    }

    /**
     * 
     * @return The HTTP body to be sent up with the Message request
     */
    public HttpEntity getEntity(){
        return null;
    };

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEndPoint.toString());
        writeNameValueListToParcel(dest, mHeaders);
    }
}
