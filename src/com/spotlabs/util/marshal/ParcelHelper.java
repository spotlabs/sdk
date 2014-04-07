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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

 class ParcelHelper {
    private Object[] out;
    private Field[] mFields;

    public ParcelHelper(Collection<Field> fields) {
        mFields = fields.toArray(new Field[fields.size()]);
        reset();
    }

    public void set(String name, Object value) {
        for (int i = 0; i < mFields.length; i++) {
            if (mFields[i].getName() == name) {
                out[i] = value;
                break;
            }
        }
    }

    public void reset() {
        out = new Object[mFields.length];
    }

    public Parcel getParcel() {
        Parcel parcel = Parcel.obtain();
        for (Object o : out) {
            Method parcelWrite = ReflectionHelper.getMethods(o.getClass()).getParcelWrite();
            try {
                parcelWrite.invoke(parcel, o);
            } catch (Exception e) {
                throw new Error("Cannot write that data to the parcel");
            }
        }
        return parcel;
    }

}
