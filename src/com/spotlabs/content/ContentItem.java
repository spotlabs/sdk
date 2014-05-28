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
 * Created by dclark on 5/27/14.
 */
public class ContentItem {

    public final int id;
    private final String sourceJson;

    public ContentItem(JSONObject jsonData) throws JSONException {
        id = jsonData.getInt("nv:Id");
        sourceJson = jsonData.toString();
    }

    @Override
    public String toString() {
        return sourceJson;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ContentItem && ((ContentItem) o).id == id;
    }
}
