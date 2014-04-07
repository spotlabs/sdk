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
package com.spotlabs.ui;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class ImageUtil {

	/**
	 * Recycles the current bitmap from a given imageView
	 * 
	 * @param view The image view which contains the bitmap to be recycled
	 * @return whether or not the bitmap has been successfully recycled. True means it has been recycled.
	 */
	public static boolean recycleBitmap(ImageView view) {
    	BitmapDrawable bd = (BitmapDrawable) view.getDrawable();
    	boolean result = recycleBitmap(bd);
    	view.setImageBitmap(null);
    	return result;
	}
	
	public static boolean recycleBitmap(BitmapDrawable drawable) {
    	if (drawable != null) {
    		drawable.getBitmap().recycle();
    		return true;
    	}
    	return false;
	}
}
