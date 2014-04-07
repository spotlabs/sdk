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

import android.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class XMLMarshaller extends Object {
    private static final String TAG = "XMLMarshaller";

    public static Object marshal() {

        return null;
    }

    public static <T extends ReflectionParcelable> T unmarshal(Element root, String packageName) throws MarshalException {
        T out;
        Class<T> outClass;

        try {
            outClass = (Class<T>) Class.forName(packageName + "." + root.getTagName());
            out = (T) outClass.newInstance();
        } catch (Exception e) {
            throw new MarshalException(MarshalException.OBJECT_CREATION);
        }

        Collection<Field> fields = ReflectionHelper.getFields(outClass);

        for (Field field : fields) {
            // grab the element that has value for us;
            NodeList values = root.getElementsByTagName(field.getName());

            if (values.getLength() < 1) continue;
            Element valueElement = (Element) values.item(0);


            Object value = null;
            try {
                field.setAccessible(true);
                if (field.getType().isArray()) {
                    // otherwise we need to do some fancy shit
                    Element[] subItems = nodeListToElements(valueElement.getChildNodes());

                    Class componentType = field.getType().getComponentType();
                    Object[] arr = (Object[]) Array.newInstance(componentType, subItems.length);

                    for (int i = 0; i < subItems.length; i++) {
                        Element e = subItems[i];
                        if (!ReflectionParcelable.class.isAssignableFrom(componentType)) {
                            arr[i] = ReflectionHelper.parseString(e.getTextContent(), componentType);
                        } else {
                            arr[i] = unmarshal(e, packageName);
                        }
                    }
                    field.set(out, arr);
                } else if (List.class.isAssignableFrom(field.getType())) {
                    Element[] subItems = nodeListToElements(valueElement.getChildNodes());
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    Class<?> componentType = (Class<?>) type.getActualTypeArguments()[0];
                    List details = (List) field.get(out);
                    for (int i = 0; i < subItems.length; i++) {
                        Element e = subItems[i];
                        if (!ReflectionParcelable.class.isAssignableFrom(componentType)) {
                            details.add(ReflectionHelper.parseString(e.getTextContent(), componentType));
                        } else {
                            details.add(unmarshal(e, packageName));
                        }
                    }
                } else {
                    field.set(out, ReflectionHelper.parseString(valueElement.getTextContent().trim(), field.getType()));

                }
            } catch (Exception e) {
                Log.w(TAG, "Error unmarshalling XML", e);
            }
            field.setAccessible(false);
        }
        return out;
    }


    private static Element[] nodeListToElements(NodeList list) {
        int elementCount = 0;
        Element[] out = new Element[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
                out[elementCount] = (Element) list.item(i);
                elementCount++;
            }
        }
        return Arrays.copyOf(out, elementCount);
    }

    // TODO: write marshal method

}
