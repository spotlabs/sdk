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

package com.spotlabs.behavior;

import android.os.RemoteException;
import android.util.Log;
import com.spotlabs.nv.behavior.IBehaviorService;

/**
 * Keeps track of application state changes and executes programmed actions when events occur.
 *
 * @author dclark
 *
 * @hide
 */
public class BehaviorService {

    private static final String TAG = "NV:BehaviorService";

    /**
     * Name of an extra that will be added to an {@link android.content.Intent} when the
     * activity launched is expected to timeout and call {@link android.app.Activity#finish()}
     *
     * Note that this will be handled automatically for applications that inherit from {@link com.spotlabs.app.NVApplication}
     *
     * @see #CANCEL_TIMEOUT_BROADCAST
     */
    public static final String TIMEOUT_EXTRA = "com.spotlabs.behavior.TIMEOUT";

    /**
     * The action of an {@link android.content.Intent} that will be broadcast if a timeout should be canceled.
     *
     * Note that this will be handled automatically for applications that inherit from {@link com.spotlabs.app.NVApplication}
     *
     * @see #TIMEOUT_EXTRA
     */
    public static final String CANCEL_TIMEOUT_BROADCAST =  "com.spotlabs.behavior.CANCEL_TIMEOUT";

    /**
     * The name of the behavior service that indicates that the device is idle(no user interaction).
     */
    public static final String IDLE_STATE = "idle";

    private final IBehaviorService mService;

    public BehaviorService(IBehaviorService service){
        mService = service;
    }

    /**
     * Tell the behavior service that the device has entered or exited the name state.
     *
     * @param state The name of the state.
     * @param inState Whether or not the device is in that state.
     */
    public void setState(String state,boolean inState){
        try{
            mService.setState(state,inState);
        }catch(RemoteException e){
            Log.w(TAG, "Error setting state on behavior manager",e);
        }
    }

    /**
     * Execute the action associated with the named event
     *
     * @param event The name of the event that occurred.
     */
    public void triggerEvent(String event) {
        try{
            mService.triggerEvent(event);
        }catch(RemoteException e){
            Log.w(TAG,"Error triggering event on behavior manager",e);
        }
    }
}
