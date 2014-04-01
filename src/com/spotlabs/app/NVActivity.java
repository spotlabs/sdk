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

package com.spotlabs.app;

/**
 * Implement this interface on your activities to interact with the NV platform.
 */
public interface NVActivity {

    /**
     * Called when NV services are available.
     *
     * This is called during the normal initialization sequence of an Activity, after <b>onCreate</b> and <b>onStart</b>
     *
     * In general, things that interact with NV services, and would normally be in onCreate or onStart, should go here.
     *
     * @param serviceManager
     */
    public abstract void onServiceManagerConnected(NVServiceManager serviceManager);

    /**
     * Called if the NV service manager disconnects(crashes)
     *
     * Should not be called under normal operation, but allows app to respond to platform disconnection.
     */
    public abstract void onServiceManagerDisconnected();
}
