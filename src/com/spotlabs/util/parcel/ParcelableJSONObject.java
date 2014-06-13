package com.spotlabs.util.parcel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dclark
 * Date: 11/6/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParcelableJSONObject extends JSONObject implements Parcelable {
    public static Creator<ParcelableJSONObject> CREATOR = new Creator<ParcelableJSONObject>() {
        @Override
        public ParcelableJSONObject createFromParcel(Parcel source) {
            try{
                return new ParcelableJSONObject(source.readString());
            }catch(JSONException e){
                Log.w("JSONParcel","Error parsing JSON from parcel?",e);
                return null;
            }
        }

        @Override
        public ParcelableJSONObject[] newArray(int size) {
            return new ParcelableJSONObject[size];  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public ParcelableJSONObject(){
        super();
    }

    public ParcelableJSONObject(String json) throws JSONException{
        super(json);
    }

    public ParcelableJSONObject(JSONObject jsonObject) throws JSONException {
        super();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()){
            String key = keys.next();
            put(key,jsonObject.get(key));
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
