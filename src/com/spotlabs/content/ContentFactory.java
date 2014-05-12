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

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dclark on 4/24/14.
 */
public class ContentFactory extends ContextWrapper{

    private static final String TAG = "ContentFactory";

    protected class ContentIterable<T> implements Iterable<T>{

        private Uri mContentUri;
        private Class<T> mClass;

        public ContentIterable(Class<T> tClass,Uri uri){
            mClass = tClass;
            mContentUri = uri;
        }

        @Override
        public Iterator<T> iterator() {
            return new ContentIterator(getContentResolver().query(mContentUri,null,null,null,null));
        }

        private class ContentIterator implements Iterator<T> {
            private Cursor mCursor;

            public ContentIterator(Cursor cursor) {
                mCursor = cursor;
            }

            @Override
            public boolean hasNext() {
                return !mCursor.isLast();
            }

            @Override
            public T next() {
                try {
                    if (mCursor.moveToNext()) {
                        JSONObject item = new JSONObject(mCursor.getString(0));
                        return createObject(mClass,item);
                    }
                }catch(JSONException e){
                    Log.w("Error parsing JSON for "+mClass,e);
                }
                return null;
            }

            @Override
            public void remove() {

            }


        }
    }

    public ContentFactory(Context base) {
        super(base);
    }

    //This is to move the generic parameter to directly on the map entry
    //Makes the code soooo much more readable.
    private static class FactoryArray<T> extends SparseArray<TypeFactory<T>>{}
    private static Map<Class,FactoryArray<?>> sFactories = new HashMap<Class, FactoryArray<?>>();

    /*
    All this mess is to get make the generic types work. Technically we could have registerSubclassFactory
    call back to registerTypeFactory, but then we'd have wrappers upon wrappers.

    This way we create one wrapper per parent that calls the original factory
     */
    private static <T> void internalRegisterTypeFactory(int typeId, Class<T> cls, TypeFactory<T> factory){
        FactoryArray<T> factories = (FactoryArray<T>) sFactories.get(cls);
        if (factories == null){
            factories = new FactoryArray<T>();
            sFactories.put(cls,factories);
        }
        factories.put(typeId, factory);
    }

    private static <T,D extends T> void registerSubclassFactory(int typeId,Class<T> cls, final TypeFactory<D> factory){
        if (cls == null) return;
        internalRegisterTypeFactory(typeId,cls,new TypeFactory<T>() {
            @Override
            public T createObject(JSONObject json) throws JSONException {
                return factory.createObject(json);
            }
        });
        registerSubclassFactory(typeId, cls.getSuperclass(), factory);
    }

    protected static <T> void registerTypeFactory(int typeId,Class<T> cls, final TypeFactory<T> factory){
        internalRegisterTypeFactory(typeId,cls,factory);
        registerSubclassFactory(typeId, cls.getSuperclass(), factory);
    }

    public static <T> T createObject(Class<T> cls, JSONObject json) throws JSONException {
        if (json == null) return null;
        int typeId = json.getInt("nv:TypeId");
        FactoryArray<T> factories = (FactoryArray<T>) sFactories.get(cls);
        if (factories == null){
            Log.w(TAG,"No factories for class "+cls );
            return null;
        }
        TypeFactory<T> factory = factories.get(typeId);
        if (factory == null){
            Log.w(TAG,"No factory for type "+typeId+" of class "+cls);
            return null;
        }
        return factory.createObject(json);
    }

    static {
        registerTypeFactory(10,Media.class,Media.factory);
        registerTypeFactory(11,Image.class,Image.factory);
    }
}