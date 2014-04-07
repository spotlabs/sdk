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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BundleMarshaller {


    public static Bundle fromXML(Element node) {
        Bundle out = new Bundle();
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element e = (Element) nodes.item(i);
                String tag = e.getTagName();
                String name = e.getAttribute("name");
                String value = e.getAttribute("value");

                if (tag.equalsIgnoreCase("int")) {
                    out.putInt(name, Integer.parseInt(value));
                } else if (tag.equalsIgnoreCase("string")) {
                    out.putString(name, value);
                } else if (tag.equalsIgnoreCase("float")) {
                    out.putFloat(e.getAttribute("name"), Float.parseFloat(value));
                } else if (tag.equalsIgnoreCase("bundle")) {
                    out.putBundle(name, fromXML(e));
                }
            }
        }

        return out;
    }

    public static String toXml(Bundle bundle) {
        //TODO: implement this
        return null;
    }

    public static byte[] toByteArray(Bundle bundle) throws IOException, IllegalAccessException, NoSuchFieldException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);

        HashMap<String, Object> map = new HashMap<String, Object>();

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (value instanceof Bundle) value = toByteArray((Bundle) value);
            map.put(key, value);
        }
        out.writeObject(map);
        return byteOut.toByteArray();
    }

    public static Bundle fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        HashMap<String, Object> map = (HashMap<String, Object>) in.readObject();
        Bundle out = new Bundle();

        // now do our reflection magic
        // first call parcel()
        Method unparcel = Bundle.class.getDeclaredMethod("unparcel");
        unparcel.setAccessible(true);
        unparcel.invoke(out);
        unparcel.setAccessible(false);

        // now jam our map into the bundle
        Field mapField = out.getClass().getDeclaredField("mMap");
        mapField.setAccessible(true);
        mapField.set(out, map);
        mapField.setAccessible(false);
        return out;
    }


    public static Bundle fromSharedPreferences(SharedPreferences preferences) {
        //TODO: implement this
        return null;
    }

    public static SharedPreferences toSharedPreferences(Context context, String name) {
        //TODO: implement this
        return null;
    }

}
