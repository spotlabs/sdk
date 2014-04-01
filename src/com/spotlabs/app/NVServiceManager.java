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

package com.spotlabs.app;

import android.os.IBinder;
import android.os.RemoteException;
import com.spotlabs.diagnostics.DiagnosticService;
import com.spotlabs.idle.IdleService;
import com.spotlabs.nv.IServiceManager;
import com.spotlabs.nv.diagnostics.IDiagnosticService;
import com.spotlabs.nv.idle.IIdleService;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to NV platform services
 *
 * @author dclark
 */
public class NVServiceManager {
    private IServiceManager mServiceManager;

    public NVServiceManager(IServiceManager serviceManager) {
        mServiceManager = serviceManager;
    }

    private Map<String,Object> mServiceCache = new HashMap<String, Object>();

    /**
     * Return an NV platform service object.
     *
     * Currently available services are:
     *
     * <dl>
     *     <dt>{@link #IDLE_SERVICE}("idle")</dt>
     *     <dd>A {@link com.spotlabs.idle.IdleService} to manage and monitor system idle state. </dd>
     *     <dt>{@link #DIAGNOSTIC_SERVICE}</dt>
     *     <dd>A {@link com.spotlabs.diagnostics.DiagnosticService} that can be used to show the system diagnostics screen.</dd>
     * </dl>
     * @param serviceName The name of the service.
     * @param <T> The type of the service.
     * @return The service or null if there is no service with that name.
     *
     * @see #IDLE_SERVICE
     * @see com.spotlabs.idle.IdleService
     * @see #DIAGNOSTIC_SERVICE
     * @see com.spotlabs.diagnostics.DiagnosticService
     */
    public <T> T getService(String serviceName){

        Object service = mServiceCache.get(serviceName);
        if (service  != null){
            return (T) service;
        }
        synchronized (mServiceCache){
            service = mServiceCache.get(serviceName);
            if (service == null){
                try{
                    ServiceFactory factory = SERVICE_MAP.get(serviceName);
                    if (factory != null){
                        service = factory.createService(mServiceManager.getService(serviceName));
                        mServiceCache.put(serviceName,service);
                    }
                }catch(RemoteException e){
                    return null;
                }
            }
        }
        return (T) service;
    }

    private interface ServiceFactory{
        Object createService(IBinder binder);
    }

    private static Map<String,ServiceFactory> SERVICE_MAP = new HashMap<String, ServiceFactory>();

    /**
     * Use with {@link #getService} to retrieve a
     * {@link com.spotlabs.idle.IdleService} to manage and monitor system idle state.
     *
     * @see #getService
     * @see
     */
    public static final String IDLE_SERVICE = "idle";

    /**
     * Use with {@link #getService} to retrieve a
     * {@link com.spotlabs.diagnostics.DiagnosticService} that can be used to show the system diagnostics screen.
     *
     * @see #getService
     * @see com.spotlabs.diagnostics.DiagnosticService
     */
    public static final String DIAGNOSTIC_SERVICE = "diagnostics";

    private static void registerServiceFactory(String serviceName, ServiceFactory serviceFactory){
        SERVICE_MAP.put(serviceName,serviceFactory);
    }

    /**
     * Get the remote interface to the NV platform service manager.
     * @return An {@link com.spotlabs.nv.IServiceManager} interface
     *
     * @see com.spotlabs.nv.IServiceManager
     */
    public IServiceManager getIServiceManager(){
        return mServiceManager;
    }

    static {
        registerServiceFactory(IDLE_SERVICE, new ServiceFactory() {
            @Override
            public Object createService(IBinder binder) {
                return new IdleService( IIdleService.Stub.asInterface(binder));
            }
        });
        registerServiceFactory(DIAGNOSTIC_SERVICE, new ServiceFactory() {
            @Override
            public Object createService(IBinder binder) {
                return new DiagnosticService(IDiagnosticService.Stub.asInterface(binder));
            }
        });
    }

}
