/*
 * ******************************************************************************
 *  * Copyright 2014 Spot Labs Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.spotlabs.settings;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import static com.spotlabs.settings.NVSettings.AUTHORITY;
/**
 * Created by dclark on 4/29/14.
 */
public class SettingGroup extends Observable{

    private static final String TAG = "SettingGroup";
    protected final ContentResolver mResolver;
    protected final Uri mContentUri;
    protected final String mGroup;

    private final ContentObserver mContentObserver;
    private Map<String,Setting> mSettings = new HashMap<String,Setting>();

    private boolean mDirty;

    protected abstract class Setting<T> extends Observable{
        private T mValue;

        public Setting(String settingName){
            mSettings.put(settingName,this);
        }

        protected abstract T parseValue(String newValue) throws Exception;

        final void setValue(String value) throws Exception{
            mValue = parseValue(value);
            setChanged();
            notifyObservers();
        }

        @Override
        public String toString(){
            //Don't shortcut this to mValue, that will skip the dirty check.
            return getValue().toString();
        }

        public T getValue(){
            if (mDirty)
                reQuery();
            return mValue;
        }
    }

    public class StringSetting extends Setting<String>{
        public StringSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected String parseValue(String newValue) throws Exception {
            return newValue;
        }
    }

    public class IntSetting extends Setting<Integer>{
        public IntSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected Integer parseValue(String newValue) throws Exception {
            return Integer.parseInt(newValue);
        }
    }

    public class BoolSetting extends Setting<Boolean>{

        public BoolSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected Boolean parseValue(String newValue) throws Exception {
            return Boolean.parseBoolean(newValue);
        }

    }

    public class TimeSetting extends Setting<Date>{

        private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

        public TimeSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected Date parseValue(String newValue) throws Exception {
            return mFormat.parse(newValue);
        }

        @Override
        public String toString() {
            return mFormat.format(getValue());
        }
   }

    public class FloatSetting extends Setting<Float>{
        public FloatSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected Float parseValue(String newValue) throws Exception {
            return Float.parseFloat(newValue);
        }
    }

    public class UriSetting extends Setting<Uri>{

        public UriSetting(String settingName) {
            super(settingName);
        }

        @Override
        protected Uri parseValue(String newValue) throws Exception {
            return Uri.parse(newValue);
        }
    }

    protected SettingGroup(String group,ContentResolver resolver, Handler updateHandler){
        mResolver = resolver;
        mGroup = group;
        mContentUri = Uri.parse("content://" + AUTHORITY + "/" + group);
        if (updateHandler != null){
            mContentObserver = new ContentObserver(updateHandler) {
                @Override
                public void onChange(boolean selfChange) {
                    mDirty = true;
                    if (countObservers() > 0){
                        reQuery();
                    }
                }
            };
            mResolver.registerContentObserver(mContentUri,true, mContentObserver);
        }else{
            mContentObserver = null;
        }
        mDirty = true;
    }

    private void reQuery() {
        synchronized (this) {
            if (!mDirty) return; //We came in after someone else blocked us.
            Cursor cursor = mResolver.query(mContentUri, new String[]{NVSettings.SettingsColumns.NAME, NVSettings.SettingsColumns.VALUE}, null, null, null);
            if (cursor != null) {
                try {
                    while (cursor.moveToNext()) {
                        Setting setting = mSettings.get(cursor.getString(0));
                        if (setting != null) {
                            try {
                                setting.setValue(cursor.getString(1));
                            } catch (Exception e) {
                                Log.w(TAG, "Error setting " + mGroup + "/" + cursor.getString(0) + " to value " + cursor.getString(1), e);
                            }
                        } else {
                            Log.w(TAG, "Unknown setting " + cursor.getString(0) + " received for group " + mGroup);
                        }
                    }
                    mDirty = false;
                } finally {
                    cursor.close();
                }
            }
            setChanged();
            notifyObservers();
        }
    }
}
