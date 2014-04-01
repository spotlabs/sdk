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

package com.spotlabs.update;

/**
 * Constants for interacting with the software update service.
 *  */
public interface UpdateService {
    /**
     * Intent that will be broadcast when an application should terminate in order for the update
     * sequence to run.
     */
    public static final String ACTION_RESET = "com.spotlabs.update.action.RESET";
}
