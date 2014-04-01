package com.spotlabs.nv.idle;

/**
 * Created by dclark on 12/6/13.
 */
interface IIdleService {
    void enableIdleTimer();
    void disableIdleTimer();
    boolean isIdle();
    void forceIdle();
}
