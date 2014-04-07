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

import java.lang.reflect.Method;

final class MarshalMethods {
    private Method mParcelRead;
    private Method mParcelWrite;
    private Method mFieldGet;
    private Method mParseString;
    private Method mConverter;

    public MarshalMethods(Method parcelRead, Method parcelWrite, Method fieldGet, Method parseString) {
        mParcelRead = parcelRead;
        mParcelWrite = parcelWrite;
        mFieldGet = fieldGet;
        mParseString = parseString;
    }


    public Method getParcelRead() {
        return mParcelRead;
    }

    public Method getParcelWrite() {
        return mParcelWrite;
    }

    public Method getFieldGet() {
        return mFieldGet;
    }

    public Method getParseString() {
        return mParseString;
    }


}
