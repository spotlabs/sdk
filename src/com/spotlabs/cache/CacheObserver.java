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
package com.spotlabs.cache;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import com.spotlabs.nv.cache.CacheContract;

/**
 * Receives call backs when a cached file has been updated.
 * 
 * @author dclark
 */
abstract class CacheObserver<T extends ChangeObserver> extends ContentObserver {

    private static final String TAG = "FileCacheObserver";

    private final Uri mUri;
    private final String mUrl;
    private final Context mContext;
    private long mLastUpdatedTime;
    private T mObserver;

    /**
     * @param context the context
     * @param handler a handler to perform the updates on
     * @param url The url to be cached and observed
     * @param persisted
     */
    CacheObserver(CacheService cache, Context context, Handler handler, String url, boolean persisted, T observer) {
        super(handler);
        mUrl = url;
        mUri = Uri.withAppendedPath(CacheContract.CONTENT_URI, url);
        mContext = context;
        mObserver = observer;
        context.getContentResolver().registerContentObserver(mUri, false, this);
        if (persisted) {
            mLastUpdatedTime = context.getSharedPreferences("cached-file-updated-times", Context.MODE_PRIVATE).getLong(url.toString(), 0);
        }
        cache.startMonitor(mUrl);
    }

    public T getObserver(){
        return mObserver;
    }

    public void unregister() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    public Uri getContentUri() {
        return mUri;
    }

    public String getUrl() {
        return mUrl;
    }

    protected ContentResolver getContentResolver() {
        return mContext.getContentResolver();
    }

    @Override
    public final void onChange(boolean selfChange) {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(mUri, new String[]{CacheContract.CacheItemColumns.LAST_MODIFIED_COLUMN}, null, null, null);
        try{
            if (cursor.moveToNext() && !cursor.isNull(0) && cursor.getLong(0) != mLastUpdatedTime) {
                mLastUpdatedTime = cursor.getLong(0);
                mContext.getSharedPreferences("cached-file-updated-times", Context.MODE_PRIVATE).edit().putLong(mUrl.toString(), mLastUpdatedTime).apply();
                onChange();
            }
        }
        finally{
            cursor.close();
        }
    }

    public static void removeWatchedFile(Context context, String originUrl) {
        context.getSharedPreferences("cached-file-updated-times", Context.MODE_PRIVATE).edit().remove(originUrl).apply();
    }
    /**
     * Notifies onChange when a new file has been changed and downloaded
     */
    protected abstract void onChange();
}
