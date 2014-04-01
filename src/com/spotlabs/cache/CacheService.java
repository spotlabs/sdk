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
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.spotlabs.nv.cache.CacheContract;
import com.spotlabs.nv.cache.ICacheService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows for offline caching of assets and synchronized updates.
 * <br>
 * 
 * @author dclark
 * @author asax
 */
public class CacheService {
    private static final String TAG = "NV:CacheService";

    private Map<String,List<CacheObserver>> mObservers = new HashMap<String, List<CacheObserver>>();

    private final ICacheService mService;

    public CacheService(ICacheService service){
        mService = service;
    }

    /**
     * Get an android content:// uri for a given remote file.
     * @param uri The Url of the remote asset.
     * @return A {@link android.net.Uri} to be used to load the asset.
     */
    public static Uri getCacheUri(String uri) {
    	return Uri.withAppendedPath(CacheContract.CONTENT_URI,uri);
    }

    /**
     * Start monitoring a remote asset for updates.
     *
     * @param uri The Url of the remote asset.
     */
    public void startMonitor(String uri) {
        startMonitor(Uri.parse(uri));
    }

    /**
     * Start monitoring a remote asset for updates.
     *
     * @param uri The Url of the remote asset.
     */
    public void startMonitor(Uri uri) {
        try{
            mService.startMonitor(uri);
        }catch(RemoteException e){
            Log.w(TAG, "Remote exception calling CacheService",e);
        }
    }

    /**
     * Add a {@link com.spotlabs.cache.CachedFileChangeObserver} for monitor a remote asset.
     *
     * @param context A {@link android.content.Context}
     * @param url The url of the remote asset.
     * @param persisted <b>true</b> if the asset is being persisted elsewhere. e.g. in a database.
     * @param observer The {@link com.spotlabs.cache.CachedFileChangeObserver} to receive notifications.
     *
     * @see com.spotlabs.cache.CachedFileChangeObserver
     */
    public synchronized void addObserver(Context context, String url, boolean persisted, CachedFileChangeObserver observer){
        List<CacheObserver> uriObservers = mObservers.get(url);
        if (uriObservers == null){
            uriObservers = new ArrayList<CacheObserver>();
            mObservers.put(url,uriObservers);
        }
        uriObservers.add(new FileCacheObserver(this,context,new Handler(context.getMainLooper()),url,persisted,observer));
    }

    /**
     * Add a {@link com.spotlabs.cache.CachedFileChangeObserver} for monitor a remote asset.
     *
     * @param context A {@link android.content.Context}
     * @param url The url of the remote asset.
     * @param handler A handler to manage the notification callback.
     * @param persisted <b>true</b> if the asset is being persisted elsewhere. e.g. in a database.
     * @param observer The {@link com.spotlabs.cache.CachedFileChangeObserver} to receive notifications.
     *
     * @see com.spotlabs.cache.CachedFileChangeObserver
     */
    public synchronized void addObserver(Context context,Handler handler, String url, boolean persisted, CachedFileChangeObserver observer) {
        List<CacheObserver> uriObservers = mObservers.get(url);
        if (uriObservers == null){
            uriObservers = new ArrayList<CacheObserver>();
            mObservers.put(url,uriObservers);
        }
        uriObservers.add(new FileCacheObserver(this,context,handler,url,persisted,observer));
    }

    /**
     * Add a {@link com.spotlabs.cache.CachedFileStreamChangeObserver} for monitor a remote asset.
     *
     * @param context A {@link android.content.Context}
     * @param url The url of the remote asset.
     * @param persisted <b>true</b> if the asset is being persisted elsewhere. e.g. in a database.
     * @param observer The {@link com.spotlabs.cache.CachedFileStreamChangeObserver} to receive notifications/
     *
     * @see com.spotlabs.cache.CachedFileChangeObserver
     */
    public synchronized void addObserver(Context context, String url, boolean persisted, CachedFileStreamChangeObserver observer){
        List<CacheObserver> uriObservers = mObservers.get(url);
        if (uriObservers == null){
            uriObservers = new ArrayList<CacheObserver>();
            mObservers.put(url,uriObservers);
        }
        uriObservers.add(new FileStreamCacheObserver(this,context,new Handler(context.getMainLooper()),url,persisted,observer));
    }

    /**
     * Add a {@link com.spotlabs.cache.CachedFileStreamChangeObserver} for monitor a remote asset.
     *
     * @param context A {@link android.content.Context}
     * @param url The url of the remote asset.
     * @param handler A handler to manage the notification callback.
     * @param persisted <b>true</b> if the asset is being persisted elsewhere. e.g. in a database.
     * @param observer The {@link com.spotlabs.cache.CachedFileStreamChangeObserver} to receive notifications/
     *
     * @see com.spotlabs.cache.CachedFileChangeObserver
     */
    public synchronized void addObserver(Context context,Handler handler, String url, boolean persisted, CachedFileStreamChangeObserver observer) {
        List<CacheObserver> uriObservers = mObservers.get(url);
        if (uriObservers == null){
            uriObservers = new ArrayList<CacheObserver>();
            mObservers.put(url,uriObservers);
        }
        uriObservers.add(new FileStreamCacheObserver(this,context,handler,url,persisted,observer));
    }

    private void removeObserverInternal(String url, ChangeObserver observer){
        List<CacheObserver> uriObservers = mObservers.get(url);
        if (uriObservers != null){
            CacheObserver toRemove = null;
            for(CacheObserver existing:uriObservers){
                if (existing.getObserver() == observer){
                    toRemove = existing;
                }
            }
            if (toRemove != null){
                uriObservers.remove(toRemove);
                toRemove.unregister();
            }
        }
    }

    /**
     * Remove a {@link com.spotlabs.cache.CachedFileChangeObserver} previously registered
     * with {@link #addObserver}
     * @param url The previously registered remote asset url
     * @param observer The previously registered observer
     */
    public synchronized void removeObserver(String url, CachedFileChangeObserver observer){
        removeObserverInternal(url,observer);
    }

    /**
     * Remove a {@link com.spotlabs.cache.CachedFileChangeObserver} previously registered
     * with {@link #addObserver}
     * @param url The previously registered remote asset url
     * @param observer The previously registered observer
     */
    public synchronized void removeObserver(String url, CachedFileStreamChangeObserver observer){
        removeObserverInternal(url,observer);
    }

}
