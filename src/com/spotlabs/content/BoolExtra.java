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

import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dclark on 6/12/14.
 */
public class BoolExtra extends Extra {
    public final boolean value;

    public BoolExtra(JSONObject jsonData) throws JSONException {
        super(jsonData);
        value = jsonData.getBoolean("value");
    }

    @Override
    public void put(Intent intent) {
        intent.putExtra(name,value);
    }

    public static TypeFactory<BoolExtra> factory = new TypeFactory<BoolExtra>() {
        @Override
        public BoolExtra createObject(JSONObject json) throws JSONException {
            return new BoolExtra(json);
        }
    };
}
