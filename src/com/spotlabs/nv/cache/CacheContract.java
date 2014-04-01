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

package com.spotlabs.nv.cache;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * 
 * 
 */
public final class CacheContract {
    public static final String AUTHORITY = "com.spotlabs.cache.provider.CacheProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY+"/cache");

    public static final class CacheItemColumns implements BaseColumns {
        public static final String URL_COLUMN = "origin_url";
        public static final String MIME_TYPE_COLUMN = "mime_type";
        public static final String LAST_ACCESSED_COLUMN = "last_accessed_time";
        public static final String LAST_MODIFIED_COLUMN = "last_modified_time";
    }
}
