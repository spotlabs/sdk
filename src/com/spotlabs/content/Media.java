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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dclark on 4/24/14.
 */
public class Media extends FileContentItem {
    static final TypeFactory<Media> factory = new TypeFactory<Media>() {
        @Override
        public Media createObject(JSONObject json) throws JSONException {
            return new Media(json);
        }
    };

    public Media(JSONObject json) throws JSONException {
        super(json);
    }
}
