package com.spotlabs.nv;

/**
 * Interface to NV platform service manager.
 */
interface IServiceManager {
    /**
     * Get a remote interface to an NV service.
     * @param serviceName The name of the service
     * @return A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
    IBinder getService(String serviceName);
    /**
     * Register a remote service with the service manager.
     * @param serviceName The name of the service.
     * @param service A {@link android.os.IBinder} used to access the service.
     * @throws android.os.RemoteException
     */
    void registerService(String serviceName, IBinder service);
}
