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

package com.spotlabs.content;

import android.content.ComponentName;
import com.spotlabs.content.ContentItem;
import com.spotlabs.content.TypeFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dclark on 5/26/14.
 */
public class Activity extends ContentItem {

    static final TypeFactory<Activity> factory = new TypeFactory<Activity>() {
        @Override
        public Activity createObject(JSONObject json) throws JSONException {
            return new Activity(json);
        }
    };

    public final ComponentName name;

    public Activity(JSONObject jsonData) throws JSONException {
        super(jsonData);
        name = new ComponentName(jsonData.getString("package"),jsonData.getString("nv:Name"));
    }
}
