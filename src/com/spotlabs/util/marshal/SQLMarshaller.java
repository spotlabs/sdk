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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLMarshaller {
    private static final String TAG = "SQLMarshaller";
    private static HashMap<Class<?>, SQLiteStatement> statements;

    public static boolean marshal(SQLiteDatabase db, Object data) {
        MarshaledValues values = getContentValues(data);
        String tableName = data.getClass().getSimpleName().toLowerCase();
        long id = db.insert(tableName, null, values.getValue());

        for (Map.Entry<String, ArrayList<ContentValues>> entry : values.getLinkedValues().entrySet()) {
            for (ContentValues v : entry.getValue()) {
                v.put(tableName + "_id", id);
                db.insert(entry.getKey(), null, v);

            }
        }

        return true;
    }

    private static MarshaledValues getContentValues(Object data) {
        MarshaledValues out = new MarshaledValues();

        Class<?> dataClass = data.getClass();

        Field[] fields = dataClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            field.setAccessible(true);
            try {
                if (field.getType().isArray() && ReflectionParcelable.class.isAssignableFrom(field.getType().getComponentType())) {
                    Object[] subItems = (Object[]) field.get(data);
                    for (Object subItem : subItems) {
                        out.addLinkedValue(subItem.getClass().getSimpleName().toLowerCase(), getContentValues(subItem).getValue());
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    List subItems = (List) field.get(data);
                    for (Object subItem : subItems) {
                        out.addLinkedValue(subItem.getClass().getSimpleName().toLowerCase(), getContentValues(subItem).getValue());
                    }
                } else {
                /*	Object b = ReflectionHelper.getMethods(type).getFieldGet().invoke(field, data);
					Method putMethod = ContentValues.class.getMethod("put", String.class, type);
					putMethod.invoke(out, field.getName(), b);*/

                    if (field.getType().equals(String.class)) {
                        out.getValue().put(field.getName(), (String) field.get(data));
                    } else if (field.getType().equals(int.class)) {
                        out.getValue().put(field.getName(), (int) field.getInt(data));
                    } else if (field.getType().equals(boolean.class)) {
                        out.getValue().put(field.getName(), (boolean) field.getBoolean(data));
                    } else if (field.getType().equals(float.class)) {
                        out.getValue().put(field.getName(), (float) field.getFloat(data));
                    }
                }
            } catch (Exception e) {
                out.toString();
            }
            field.setAccessible(false);
        }

        return out;
    }

    public static <T> T unmarshal(Cursor cursor, SQLiteDatabase db, Class<? extends T> outClass) {
        String[] columns = cursor.getColumnNames();

        T out = null;
        try {
            out = outClass.newInstance();
        } catch (Exception e) {
            return out;
        }

        Collection<Field> fields = ReflectionHelper.getFields(outClass);

        for (Field field : fields) {

            int columnIndex = cursor.getColumnIndex(field.getName());
            Class<?> type = field.getType();
            if (columnIndex == -1 && !type.isArray()) //Skip non-detail fields that aren't in the table.
                continue;
            field.setAccessible(true);

            Object value = null;

            if (type.equals(String.class)) {
                value = cursor.getString(columnIndex);
            } else if (type.equals(int.class)) {
                value = cursor.getInt(columnIndex);
            } else if (type.equals(boolean.class)) {
                value = cursor.getInt(columnIndex) == 1 ? true : false;
            } else if (type.equals(float.class)) {
                value = cursor.getFloat(columnIndex);
            } else if (type.isArray() &&
                    ReflectionParcelable.class.isAssignableFrom(field.getType().getComponentType())) {

                Cursor c = db.query(type.getComponentType().getSimpleName(), null,
                        outClass.getSimpleName() + "_id = ?",
                        new String[]{cursor.getString(cursor.getColumnIndex("id"))},
                        null, null, null);


                Object[] arr = (Object[]) Array.newInstance(type.getComponentType(), c.getCount());

                c.moveToFirst();
                int r = 0;
                while (!c.isAfterLast()) {
                    arr[r] = unmarshal(c, db, field.getType().getComponentType());
                    r++;
                    c.moveToNext();
                }
                value = arr;
            }

            try {
                field.set(out, value);
                field.setAccessible(false);
            } catch (Exception e) {
                Log.w(TAG, "Error setting value during unmarshal", e);
                System.out.println(e.getMessage());
            }
        }
        cursor.moveToNext();
        return out;
    }

    public static <T> T unmarshal(Cursor cursor, SQLiteDatabase db, Class<? extends T> outClass, Class<?>[] detailClasses, Cursor[] detailCursors, String[] detailFieldNames) {
        T result = unmarshal(cursor, db, outClass);
        int id;
        try {
            Field idField = ReflectionHelper.getField(outClass, "id");
            idField.setAccessible(true);
            id = (Integer) idField.get(result);
            idField.setAccessible(false);
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Error retrieving master id during unmarshal", e);
            throw new IllegalAccessError(e.getMessage());
        }
        for (int i = 0; i < detailClasses.length; i++) {
            int idColumnIndex = detailCursors[i].getColumnIndex(outClass.getSimpleName().toLowerCase() + "_id");
            List details;
            try {
                Field detailField = ReflectionHelper.getField(outClass, detailFieldNames[i]);
                detailField.setAccessible(true);
                details = (List) detailField.get(result);
                detailField.setAccessible(false);
            } catch (IllegalAccessException e) {
                Log.w(TAG, "Error retrieving detail field during unmarshal", e);
                throw new IllegalAccessError(e.getMessage());
            }
            while (!detailCursors[i].isAfterLast() && detailCursors[i].getInt(idColumnIndex) == id) {
                details.add(unmarshal(detailCursors[i], db, detailClasses[i]));
            }
        }
        return result;
    }

    private static class MarshaledValues {
        private ContentValues mValue;
        private HashMap<String, ArrayList<ContentValues>> mLinkedValues;

        public MarshaledValues() {
            mValue = new ContentValues();
            mLinkedValues = new HashMap<String, ArrayList<ContentValues>>();
        }

        public void addLinkedValue(String table, ContentValues value) {
            if (!mLinkedValues.containsKey(table)) mLinkedValues.put(table, new ArrayList<ContentValues>());
            mLinkedValues.get(table).add(value);
        }

        public HashMap<String, ArrayList<ContentValues>> getLinkedValues() {
            return mLinkedValues;
        }

        public ContentValues getValue() {
            return mValue;
        }
    }

}
