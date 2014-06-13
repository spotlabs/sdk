package com.spotlabs.util.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by dclark on 2/17/14.
 */
public class ParcelableJSONArray extends JSONArray implements Parcelable {

    public static Creator<ParcelableJSONArray> CREATOR = new Creator<ParcelableJSONArray>() {
        @Override
        public ParcelableJSONArray createFromParcel(Parcel source) {
            try{
                return new ParcelableJSONArray(source.readString());
            }catch(JSONException e){
                Log.w("JSONParcel", "Error parsing JSON from parcel?", e);
                return null;
            }
        }

        @Override
        public ParcelableJSONArray[] newArray(int size) {
            return new ParcelableJSONArray[size];  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public ParcelableJSONArray(String json) throws JSONException{
        super(json);
    }

    public ParcelableJSONArray(JSONArray array) throws JSONException {
        super();
        for(int i=0;i<array.length();i++){
            this.put(i,array.get(i));
        }
    }

    @Override
    public int describeContents() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.toString());
    }
}
