/*
 * Copyright 2014 Spot Labs Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.spotlabs.idle;

import android.util.Log;
import com.spotlabs.nv.idle.IIdleService;

/**
 * Manages behavior when device goes idle.
 *
 * @author dclark
 */
public class IdleService {

    private static final String TAG = "SpotLabs:IdleService";

    private final IIdleService mService;

    public IdleService(IIdleService service){
        mService = service;
    }

    /**
     * Get idle state information
     *
     * @return <b>true</b> if the device is currently idle
     */
    public boolean isIdle() {
        try {
            return mService.isIdle();
        } catch (Exception e) {
            Log.w(TAG, "Error retrieving idle state", e);
        }
        return false;
    }

    /**
     * Disable the idle timeout
     *
     */
    public void disableIdleTimer() {
        try {
            mService.disableIdleTimer();
        } catch (Exception e) {
            Log.w(TAG, "Error disabling idle timer", e);
        }
    }

    /**
     * Re-enable the idle timer.
     */
    public void enableIdleTimer() {
        try {
            mService.enableIdleTimer();
        } catch (Exception e) {
            Log.w(TAG, "Error enabling idle state", e);
        }
    }

    /**
     * Force the device into the idle state.
     */
    public void forceIdle(){
        try{
            mService.forceIdle();
        }catch(Exception e){
            Log.w(TAG, "Error enabling idle state", e);
        }
    }
}
