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
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;

import java.util.Observable;

/**
 * Created by dclark on 4/29/14.
 */
public class NVSettings extends Observable{

    public static final String AUTHORITY = "com.spotlabs.provider.Settings";

    private final ContentObserver mContentObserver;

    public static final class SettingsColumns implements BaseColumns {
        public static final String NAME = "name";
        public static final String VALUE = "value";
    }

    public static Uri getContentUri(String group){
        return Uri.parse("content://" + AUTHORITY+"/"+group);
    }

    public NVSettings(ContentResolver resolver, Handler updateHandler){
        if (updateHandler != null){
            mContentObserver = new ContentObserver(updateHandler){
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    setChanged();
                    notifyObservers();
                }
            };
            resolver.registerContentObserver(Uri.parse("content://"+AUTHORITY),true,mContentObserver);
        }else{
            mContentObserver = null;
        }
    }
}
