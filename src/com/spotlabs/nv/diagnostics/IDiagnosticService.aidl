package com.spotlabs.nv.diagnostics;

/**
 * Interface to NV platform diagnostics service.
 */
interface IDiagnosticService {

    /**
     * Display the diagnostic screen if provided the correct PIN
     *
     * @param pin the PIN code for this device
     * @return <b>true</b> if the correct PIN was provided.
     * */
    boolean showDiagnostics(String pin);
}
