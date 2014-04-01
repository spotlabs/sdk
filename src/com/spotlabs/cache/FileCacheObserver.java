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

import android.content.Context;
import android.os.Handler;

/**
 * Receives call backs when a cached file has been updated.
 * 
 * @author dclark
 */
class FileCacheObserver extends CacheObserver {

    private static final String TAG = "FileCacheObserver";

    private final CachedFileChangeObserver mFileChanged;

    
    /**
     * @param context the context
     * @param handler a handler to perform the updates on
     * @param url The url to be cached and observed
     * @param persisted
     */
    FileCacheObserver(CacheService cache, Context context, Handler handler, String url, boolean persisted, CachedFileChangeObserver fileChanged) {
        super(cache,context,handler,url,persisted,fileChanged);
        mFileChanged = fileChanged;
        dispatchChange(false,null);
    }


    @Override
    protected void onChange() {
        mFileChanged.onChange(getUrl());
    }
}
