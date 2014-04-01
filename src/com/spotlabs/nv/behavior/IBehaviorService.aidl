package com.spotlabs.nv.behavior;

/**
 * Created by dclark on 2/13/14.
 */
interface IBehaviorService {

    void setState(String state, boolean inState);
    boolean getState(String state);
    oneway void triggerEvent(String event);
}
