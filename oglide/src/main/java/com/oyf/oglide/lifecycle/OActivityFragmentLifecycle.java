package com.oyf.oglide.lifecycle;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 14:29
 * @描述
 **/
public class OActivityFragmentLifecycle implements OLifecycle, OLifecycleListener {
    private Set<OLifecycleListener> lifecycleListeners = Collections.newSetFromMap(new WeakHashMap<OLifecycleListener, Boolean>());

    private boolean isStarted;
    private boolean isDestroyed;

    @Override
    public void addListener(OLifecycleListener oLifecycleListener) {
        lifecycleListeners.add(oLifecycleListener);
        if (isStarted) {
            oLifecycleListener.onStart();
        } else if (isDestroyed) {
            oLifecycleListener.onDestroy();
        } else {
            oLifecycleListener.onStop();
        }
    }

    @Override
    public void removeListener(OLifecycleListener oLifecycleListener) {
        lifecycleListeners.remove(oLifecycleListener);
    }

    @Override
    public void onStart() {
        isStarted = true;
        for (OLifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStart();
        }
    }

    @Override
    public void onStop() {
        isStarted = false;
        for (OLifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStop();
        }
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        for (OLifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onDestroy();
        }
    }
}
