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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dclark on 6/12/14.
 */
public class LaunchAction extends Action {
    public final Activity activity;
    public final Iterable<Extra> extras;

    public LaunchAction(JSONObject jsonData) throws JSONException {
        super(jsonData);
        activity = ContentFactory.createObject(Activity.class,jsonData.getJSONObject("activity"));

        JSONArray extraArray = jsonData.optJSONArray("extras");
        if (extraArray != null){
            List<Extra> extraList = new ArrayList<Extra>();
            for (int i =0;i<extraArray.length();i++){
                extraList.add(ContentFactory.createObject(Extra.class,extraArray.getJSONObject(i)));
            }
            this.extras = extraList;
        }else{
            this.extras = new ArrayList<Extra>();
        }
    }

    public static TypeFactory<LaunchAction> factory = new TypeFactory<LaunchAction>() {
        @Override
        public LaunchAction createObject(JSONObject json) throws JSONException {
            return new LaunchAction(json);
        }
    };

}
