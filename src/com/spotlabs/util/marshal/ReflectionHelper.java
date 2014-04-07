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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ReflectionHelper {

    private final static Map<Class<?>, Map<String, Field>> classFields = new HashMap<Class<?>, Map<String, Field>>();
    private final static Map<Class<?>, MarshalMethods> classMethods = new HashMap<Class<?>, MarshalMethods>() {
        {
            try {
                put(int.class, new MarshalMethods(
                        Parcel.class.getMethod("readInt"),
                        Parcel.class.getMethod("writeInt", int.class),
                        Field.class.getMethod("getInt", Object.class),
                        Integer.class.getMethod("parseInt", String.class)
                ));

                put(float.class, new MarshalMethods(
                        Parcel.class.getMethod("readFloat"),
                        Parcel.class.getMethod("writeFloat", float.class),
                        Field.class.getMethod("get", Object.class),
                        Float.class.getMethod("parseFloat", String.class)
                ));

                put(String.class, new MarshalMethods(
                        Parcel.class.getMethod("readString"),
                        Parcel.class.getMethod("writeString", String.class),
                        Field.class.getMethod("get", Object.class),
                        null
                ));
            } catch (NoSuchMethodException e) {
                throw new Error("Error creating MarshalMethods for ReflectionHelper. " + e.getMessage());
            }
        }
    };

    private ReflectionHelper() {
    }

    private static void loadFields(Class<?> parcelableClass) {
        HashMap<String, Field> fields = new HashMap<String, Field>();
        for (Field field : parcelableClass.getDeclaredFields()) {
            fields.put(field.getName(), field);
        }
        classFields.put(parcelableClass, fields);
    }

    public static Collection<Field> getFields(Class<?> parcelableClass) {
        if (!classFields.containsKey(parcelableClass)) {
            loadFields(parcelableClass);
        }
        return classFields.get(parcelableClass).values();
    }

    public static Field getField(Class<?> parcelableClass, String fieldName) {
        if (!classFields.containsKey(parcelableClass)) {
            loadFields(parcelableClass);
        }
        return classFields.get(parcelableClass).get(fieldName);
    }

    public static MarshalMethods getMethods(Class<?> type) {
        return classMethods.get(type);
    }

    public static Object parseString(String value, Class<?> outType) {
        if (!classMethods.containsKey(outType) || classMethods.get(outType).getParseString() == null) return value;
        try {
            return classMethods.get(outType).getParseString().invoke(null, value);
        } catch (Exception e) {
            return null;
        }
    }

    static ParcelHelper getParcelHelper(Class<?> parcelableClass) {
        return new ParcelHelper(getFields(parcelableClass));
    }
}
