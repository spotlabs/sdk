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

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public abstract class ReflectionParcelable implements Parcelable {

	/*
     * Goes to ReflectionParsible to get fields and types
	 * create array the same size as the parcible list
	 * do a lookup to get the index
	 * inset the named data into specific index
	 */

    public ReflectionParcelable() {

    }

    public ReflectionParcelable(Parcel source) {
        Collection<Field> fields = ReflectionHelper.getFields(getClass());

        for (Field field : fields) {
            Class type = field.getType();
            Method getParcel = ReflectionHelper.getMethods(type).getParcelRead();
            field.setAccessible(true);
            try {
                field.set(this, getParcel.invoke(source));
            } catch (Exception e) {
            }
            field.setAccessible(false);
        }
    }


    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (Field field : ReflectionHelper.getFields(getClass())) {
            Class type = field.getType();
            MarshalMethods m = ReflectionHelper.getMethods(type);
            try {
                m.getParcelWrite().invoke(dest, m.getFieldGet().invoke(field, this));
            } catch (Exception e) {
            }
        }
    }
}


