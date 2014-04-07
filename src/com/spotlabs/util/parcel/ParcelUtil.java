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
package com.spotlabs.util.parcel;

import android.os.Parcel;
import android.os.ResultReceiver;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of tools for creating/decoding parcels for specific data types.
 */
public final class ParcelUtil {

    public static void writeNameValueListToParcel(Parcel dest, List<NameValuePair> data){
        dest.writeInt(data.size());
        for(NameValuePair item:data){
            dest.writeString(item.getName());
            dest.writeString(item.getValue());
        }
    }

    public static List<NameValuePair> readNameValueListFromParcel(Parcel source){
        int size = source.readInt();
        List<NameValuePair> result = new ArrayList<NameValuePair>();
        for (int i =0;i<size;i++){
            result.add(new BasicNameValuePair(source.readString(),source.readString()));
        }
        return result;
    }

    public static ResultReceiver getIpcReceiver(ResultReceiver receiver){
        Parcel temp = Parcel.obtain();
        receiver.writeToParcel(temp,0);
        temp.setDataPosition(0);
        ResultReceiver result = ResultReceiver.CREATOR.createFromParcel(temp);
        temp.recycle();
        return result;
    }
}
