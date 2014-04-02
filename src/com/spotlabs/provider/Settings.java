/*
 * Copyright 2014 Spot Labs Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.spotlabs.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Provides system configurations such as backend server urls, device ids, etc.
 *
 * Modeled after {@link android.content.ContentQueryMap}, but specialized for settings.
 * 
 * @author dclark
 */
public class Settings extends Observable {

    /**
     * Content authority for NV settings provider;
     */
    public static final String AUTHORITY = "com.spotlabs.provider.Settings";
    private boolean mDirty;

    public static final class SettingsColumns implements BaseColumns{
        public static final String NAME = "name";
        public static final String VALUE = "value";
    }

    protected final ContentResolver mResolver;
    protected final Uri mContentUri;
    private final ContentObserver mContentObserver;
    private Map<String,String> mCache = new HashMap<String,String>();

    /**
     * Create a new {@link com.spotlabs.provider.Settings} instance for the current package.
     *
     * @param context A {@link Context} for this instance
     * @param updateHandler A {@link android.os.Handler} to respond to setting updates.
     */
    public Settings(Context context, Handler updateHandler){
        this(context.getPackageName(),context.getContentResolver(),updateHandler);
    }

    /**
     * Create a new {@link com.spotlabs.provider.Settings} instance for a given package.
     *
     * Note:
     *  Only settings for the current package, or another package owned by the same user may be obtained.
     *
     * @param pkg The name of the package to get settings for.
     * @param resolver A {@link android.content.ContentResolver} to use to obtain settings data.
     * @param updateHandler A {@link android.os.Handler} to respond to setting updates.
     */
    public Settings(String pkg, ContentResolver resolver, Handler updateHandler){
        mResolver = resolver;
        mContentUri = Uri.parse("content://" + AUTHORITY+"/"+pkg);
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

    private void reQuery(){
        synchronized (this){
            if (!mDirty) return; //We came in after someone else blocked us.
            Cursor cursor = mResolver.query(mContentUri,new String[] {SettingsColumns.NAME, SettingsColumns.VALUE},null,null,null);
            try{
                mCache = new HashMap<String,String>(mCache.size());
                while(cursor.moveToNext()){
                    mCache.put(cursor.getString(0),cursor.getString(1));
                }
                mDirty = false;
            }finally{
                cursor.close();
            }
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Get a setting
     *
     * @param name The name of the setting
     * @return The setting as an integer.
     */
    public int getInt(String name){
        if (mDirty) reQuery();
        return Integer.parseInt(mCache.get(name));
    }

    /**
     * Get a setting
     *
     * @param name The name of the setting
     * @return The setting as a string.
     */
    public String getString(String name){
        if (mDirty) reQuery();
        return mCache.get(name);
    }

    /**
     * Get a setting
     *
     * @param name The name of the setting
     * @return The setting as an {@link android.net.Uri}
     */
    public Uri getUri(String name){
        if (mDirty) reQuery();
        return Uri.parse(mCache.get(name));
    }

    /**
     * Get a setting
     *
     * @param name The name of the setting
     * @return The setting as an integer.
     */
    public boolean getBoolean(String name){
        if (mDirty) reQuery();
        return Boolean.parseBoolean(mCache.get(name));
    }

    /**
     * Get a setting
     *
     * @param name The name of the setting
     * @return The setting as an float.
     */
    public float getFloat(String name){
        if (mDirty) reQuery();
        return Float.parseFloat(mCache.get(name));
    }

    /**
     * Write a setting
     *
     * @param name The name of the setting
     * @return The setting
     */
    public void put(String name, String value){
        ContentValues values = new ContentValues(2);
        values.put(SettingsColumns.NAME,name);
        values.put(SettingsColumns.VALUE,value);
        mResolver.insert(mContentUri,values);
        synchronized (this){
            mDirty = true;
        }
    }

    /**
     * Write a setting
     *
     * @param name The name of the setting
     * @return The setting
     */
    public void put(String name,int value){
        put(name, Integer.toString(value));
    }

    /**
     * Write a setting
     *
     * @param name The name of the setting
     * @return The setting
     */
    public void put(String name, Uri value){
        put(name, value.toString());
    }

    /**
     * Write a setting
     *
     * @param name The name of the setting
     * @return The setting
     */
    public void put(String name, boolean value){
        put(name, Boolean.toString(value));
    }

    /**
     * Write a setting
     *
     * @param name The name of the setting
     * @return The setting
     */
    public void put(String name, float value) {
        put(name, Float.toString(value));
    }

    /**
     * Close this instance, and stop monitoring setting changes.
     */
    public void close(){
        if (mContentObserver != null){
            mResolver.unregisterContentObserver(mContentObserver);
        }
    }
}
