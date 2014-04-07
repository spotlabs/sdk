/*******************************************************************************
 * Copyright 2013 Spot Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.spotlabs.util.marshal;

public class MarshalException extends Exception {

    public static final String OBJECT_CREATION = "Cannot create your object.";
    public static final String SETTING_FIELD = "Could not set field.";

    private String mMessage;

    public MarshalException(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
