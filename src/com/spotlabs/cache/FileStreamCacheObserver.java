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
import android.os.Handler;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Same as {@link FileCacheObserver} except loads changed file into input stream.
 * 
 * @author dclark
 */
class FileStreamCacheObserver extends CacheObserver{
    private static final String TAG = "FileStreamCacheObserver";

    private final CachedFileStreamChangeObserver mFileStreamChanged;

    FileStreamCacheObserver(CacheService cacheService, Context context, Handler handler, String originUrl, boolean persisted, CachedFileStreamChangeObserver streamChanged) {
        super(cacheService,context, handler, originUrl,persisted,streamChanged);
        mFileStreamChanged = streamChanged;
        dispatchChange(false,null);
    }


    @Override
    protected void onChange() {
        ContentResolver resolver = getContentResolver();
        try
        {
            InputStream stream = resolver.openInputStream(getContentUri());
            try{
                mFileStreamChanged.onChange(getUrl(),stream);
            }
            catch(Exception e){
                Log.w(TAG, "Error processing file update on " + getUrl(), e);
            }
            finally
            {
                try{
                    stream.close();
                }
                catch(IOException e){
                    Log.w(TAG, "Error closing cached file stream", e);
                }
            }
        }
        catch(FileNotFoundException e){
                Log.w(TAG, "Cache service answered query with time, but didn't return file",e);
            }
    }
}
