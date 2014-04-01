/*
 * Copyright 2014 Spot Labs Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.spotlabs.diagnostics;

import android.os.RemoteException;
import android.util.Log;
import com.spotlabs.nv.diagnostics.IDiagnosticService;

/**
 * Provide access to the diagnostic screen
 *
 * @author dclark
 * @author asax
 */
public class DiagnosticService {

    private static final String TAG="NVCore:DiagnosticService";

    private IDiagnosticService mService;

    public DiagnosticService(IDiagnosticService diagnosticService) {
        mService = diagnosticService;
    }

    /**
     * Display the diagnostic screen if provided the correct PIN
     *
     * @param pin the PIN code for this device
     * @return <b>true</b> if the correct PIN was provided.
     * */
    public boolean showDiagnosticScreen(String pin){
        try{
            return mService.showDiagnostics(pin);
        }catch(RemoteException e){
            Log.w(TAG, "Error calling diagnostic service.", e);
        }
        return false;
    }
}





