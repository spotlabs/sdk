/*******************************************************************************
 * Copyright 2013 Spot Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.spotlabs.app;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.spotlabs.behavior.BehaviorService;
import com.spotlabs.diagnostics.DiagnosticService;
import com.spotlabs.idle.IdleService;
import com.spotlabs.nv.IServiceManager;
import com.spotlabs.settings.NVSettings;
import com.spotlabs.update.UpdateService;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 *  NVApplication extends the default Android {@link Application} to provide for basic plumbing into the nv-platform.
 *  <p/>
 *  To utilize NVApplication update your {@code AndroidManifest.xml} specify a name for your application node.
 *  <pre>
 *  {@code
 *   <application 
 *   	...
 *   	android:name="com.spotlabs.app.NVApplication"
 *   	...>}</pre>
 *  This application requires the {@link android.permission.SYSTEM_ALERT_WINDOW} permission  
 *   
 *  By using this class you enable: 
 *  <br>
 *  <ul>
 *  	<li>idle device detection</li>
 *  	<li>launching into diagnostics mode</li>
 *  	<li>accessing system settings</li>
 *  </ul>
 *  
 *  
 *  This class should be used in any final deployment of a nvapp.
 *   
 * @author Adam Sax
 * 
 */
public abstract class NVApplicationBase<SettingsClass extends NVSettings> extends Application {

    private static final int MSG_TIMEOUT = 1;
    private static final int MSG_CONNECT = 2;
    private static String TAG = "NVApplication";

    private WindowManager mWindowManager;
    private View mTouchDetector;
    private Rect mScreenRect = new Rect();
    private static NVApplicationBase sInstance;

    private static final int DIAGNOSTICS_MARGIN = 150;
    private static final int NOT_STARTED = 0;
    private static final int UPPER_LEFT_COMPLETED = 1;
    private static final int UPPER_RIGHT_COMPLETED = 2;
    private int mDiagnosticGestureState = NOT_STARTED;
    private Activity mCurrentActivity;
    private Map<Class<? extends NVActivity>,NVActivity> mActivities = new HashMap<Class<? extends NVActivity>, NVActivity>();

    private NVServiceManager mServiceManager;
    private IdleService mIdleService;
    private Handler mHandler = new ApplicationHandler();
    private SettingsClass mSettings;

    public NVApplicationBase() {
        sInstance = this;
    }

    private class ApplicationHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_TIMEOUT:
                    ((Activity) msg.obj).finish();
                    break;
                case MSG_CONNECT:
                    ((NVActivity) msg.obj).onServiceManagerConnected(mServiceManager);
                    break;
            }
        }
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(mDiagnosticGestureState){
                case NOT_STARTED:
                    if (event.getX() < DIAGNOSTICS_MARGIN && event.getY() < DIAGNOSTICS_MARGIN){
                        mDiagnosticGestureState = UPPER_LEFT_COMPLETED;
                    }
                    break;
                case UPPER_LEFT_COMPLETED:
                    if (event.getY() < DIAGNOSTICS_MARGIN){
                        if (event.getX() > mScreenRect.width()-DIAGNOSTICS_MARGIN){
                            mDiagnosticGestureState = UPPER_RIGHT_COMPLETED;
                        }
                    }else{
                        mDiagnosticGestureState = NOT_STARTED;
                    }
                    break;
                case UPPER_RIGHT_COMPLETED:
                    if (event.getX() > mScreenRect.width()-DIAGNOSTICS_MARGIN){
                        if (event.getY() > mScreenRect.height()-DIAGNOSTICS_MARGIN){
                            showDiagnosticsScreen();
                        }
                    }else{
                        mDiagnosticGestureState = NOT_STARTED;
                    }
            }
            return false;
        }
    };

    protected abstract SettingsClass createSettings(ContentResolver resolver, Handler handler);

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityCallback());
        mSettings = createSettings(getContentResolver(),mHandler);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mWindowManager.getDefaultDisplay().getRectSize(mScreenRect);
        mTouchDetector = new View(this);
        mTouchDetector.setOnTouchListener(mTouchListener);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(0, 0,WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSPARENT);
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowManager.addView(mTouchDetector, lp);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        },new IntentFilter(UpdateService.ACTION_RESET));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mHandler.removeMessages(MSG_TIMEOUT);
            }
        },new IntentFilter(BehaviorService.CANCEL_TIMEOUT_BROADCAST));
        bindService(new Intent("com.spotlabs.nv.IServiceManager"), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                onServiceManagerConnected(IServiceManager.Stub.asInterface(service));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                for (NVActivity activity:mActivities.values()){
                    activity.onServiceManagerDisconnected();
                }
            }
        },BIND_AUTO_CREATE);
    }

    public NVServiceManager getServiceManager(){
        return mServiceManager;
    }

    protected final void onServiceManagerConnected(IServiceManager service) {
        mServiceManager = new NVServiceManager(service);
        mIdleService = mServiceManager.getService(NVServiceManager.IDLE_SERVICE);
        onServiceManagerConnected();
        for (NVActivity activity:mActivities.values()){
            activity.onServiceManagerConnected(mServiceManager);
        }
    }

    protected void onServiceManagerConnected(){
    }

    @Override
    public void onTerminate() {
        mWindowManager.removeView(mTouchDetector);
        super.onTerminate();
    }

    /**
     * Register a typeface with the system.
     *
     * @param family The name of the the typeface family
     * @param assetManager An {@link android.content.res.AssetManager} to use for loading.
     * @param path The path to the font asset.
     */
    protected void registerTypeface(String family, AssetManager assetManager, String path){
        try {
            Method method = Typeface.class.getMethod("registerTypeface", String.class, AssetManager.class,String.class);
            method.invoke(null,family,assetManager,path);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "Error reading typeface asset", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG,"Error registering typeface",e);
        } catch (IllegalAccessException e) {
            Log.w(TAG,"Error registering typeface",e);
        }
    }

    /**
     * Register a typeface with the system
     *
     * @param family The name of the the typeface family
     * @param file A {@link java.io.File} referencing the font file.
     */
    protected void registerTypeface(String family, File file){
        try {
            Method method = Typeface.class.getMethod("registerTypeface", String.class, File.class);
            method.invoke(null,family,file);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "registerTypeface is not available on non Spot Labs devices.");
        } catch (InvocationTargetException e) {
            Log.w(TAG,"Error registering typeface",e);
        } catch (IllegalAccessException e) {
            Log.w(TAG,"Error registering typeface",e);
        }
    }

    /**
     * Register a typeface with the system
     *
     * @param family The name of the the typeface family
     * @param file The path to the font file.
     */
    protected void registerTypeface(String family, String file){
        try {
            Method method = Typeface.class.getMethod("registerTypeface", String.class, String.class);
            method.invoke(null,family,file);
        } catch (NoSuchMethodException e) {
            Log.i(TAG,"registerTypeface is not available on non Spot Labs devices.");
        } catch (InvocationTargetException e) {
            Log.w(TAG,"Error registering typeface",e);
        } catch (IllegalAccessException e) {
            Log.w(TAG,"Error registering typeface",e);
        }
    }

    /**
     * @hide
     */
    private void showDiagnosticsScreen() {
        DiagnosticService diagnosticService = mServiceManager.getService(NVServiceManager.DIAGNOSTIC_SERVICE);
        diagnosticService.showDiagnosticScreen(null);
    }

    protected Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    /**
     * Gets the current idle state.
     * @return
     */
    public boolean isIdle(){
        if (mIdleService == null) return true;
        return mIdleService.isIdle();
    }

    /**
     * Disables the idle timer.
     */
    public void disableIdleTimer() {
        mIdleService.disableIdleTimer();
    }

    /**
     * enables the idle timers
     */
    public void enableIdleTimer() {
        mIdleService.enableIdleTimer();
    }

    public SettingsClass getSettings(){
        return mSettings;
    }

    public static NVApplicationBase getInstance() {
        return sInstance;
    }

    private class ActivityCallback implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof NVActivity){
                NVActivity nvActivity = (NVActivity) activity;
                mActivities.put(nvActivity.getClass(),nvActivity);
                //This actually happens during the call to super.onCreate
                //Makes for some fun timing problems, where sometimes connected is called the next cycle
                //after onCreate/onStart/OnResume, or in the middle.
                //We'll make it consistent.
                if (mServiceManager != null){
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_CONNECT,activity));
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            int idleTimeout = activity.getIntent().getIntExtra(BehaviorService.TIMEOUT_EXTRA,-1);
            if (idleTimeout != -1){
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TIMEOUT,activity),idleTimeout*1000);
            }
            mCurrentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity == mCurrentActivity){
                mCurrentActivity = null;
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activity.getIntent();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivities.remove(activity);
        }
    }
}
