package com.oyf.oglide;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.oyf.oglide.fragment.ORequestManagerFragment;
import com.oyf.oglide.fragment.OSupportRequestManagerFragment;
import com.oyf.oglide.lifecycle.OLifecycle;
import com.oyf.oglide.lifecycle.OLifecycleListener;
import com.oyf.oglide.utils.OPreconditions;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 14:06
 * @描述
 **/
public class ORequestManagerRetriever implements Handler.Callback {
    private static final String FRAGMENT_SUPPORT_TAG = "FRAGMENT_SUPPORT_TAG";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
    private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;

    private final Handler handler;
    private ORequestManager applicationManager;

    public ORequestManagerRetriever() {
        handler = new Handler(Looper.getMainLooper(), this);
    }

    public ORequestManager get(Activity activity) {
        if (!OPreconditions.isMainThread()) {
            return get(activity.getApplicationContext());
        } else {
            FragmentManager fragmentManager = activity.getFragmentManager();
            return fragmentGet(activity, fragmentManager);
        }
    }

    public ORequestManager get(FragmentActivity fragmentActivity) {
        if (!OPreconditions.isMainThread()) {
            return get(fragmentActivity.getApplicationContext());
        } else {
            androidx.fragment.app.FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
            return supportFragmentGet(fragmentActivity, supportFragmentManager);
        }
    }

    public ORequestManager get(Context context) {
        if (null == applicationManager) {
            OLifecycle oLifecycle = new OLifecycle() {
                @Override
                public void addListener(OLifecycleListener listener) {
                    listener.onStart();
                }

                @Override
                public void removeListener(OLifecycleListener listener) {

                }
            };
            applicationManager = new ORequestManager(OGlide.get(context), oLifecycle, context);
        }
        return applicationManager;
    }

    /**
     * 获取包下面的fragment
     *
     * @param activity
     * @param fragmentManager
     * @return
     */
    private ORequestManager fragmentGet(Activity activity, FragmentManager fragmentManager) {
        ORequestManagerFragment requestManagerFragment = getRequestManagerFragment(fragmentManager);
        ORequestManager oRequestManager = requestManagerFragment.getORequestManager();
        if (null == oRequestManager) {
            oRequestManager = new ORequestManager(OGlide.get(activity), requestManagerFragment.getGlideLifecycle(), activity);
            requestManagerFragment.setORequestManager(oRequestManager);
        }
        return oRequestManager;
    }

    private ORequestManagerFragment getRequestManagerFragment(FragmentManager fragmentManager) {
        ORequestManagerFragment fragmentByTag = (ORequestManagerFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (null == fragmentByTag) {
            fragmentByTag = new ORequestManagerFragment();
            fragmentManager.beginTransaction().add(fragmentByTag, FRAGMENT_TAG).commitAllowingStateLoss();
            //需要使用handler发送一条消息，使其添加成功
            handler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fragmentManager).sendToTarget();
        }
        return fragmentByTag;
    }

    /**
     * 获取v4包下面的fragment
     *
     * @param context
     * @param supportFragmentManager
     * @return
     */
    private ORequestManager supportFragmentGet(Context context, androidx.fragment.app.FragmentManager supportFragmentManager) {
        OSupportRequestManagerFragment supportRequestManagerFragment = getSupportRequestManagerFragment(supportFragmentManager);
        ORequestManager oRequestManager = supportRequestManagerFragment.getORequestManager();
        if (null != supportRequestManagerFragment) {
            OGlide oGlide = OGlide.get(context);
            oRequestManager = new ORequestManager(oGlide, supportRequestManagerFragment.getGlideLifecycle(), context);
            supportRequestManagerFragment.setORequestManager(oRequestManager);
        }
        return oRequestManager;
    }

    /**
     * 创建监听生命周期的fragment
     */
    private OSupportRequestManagerFragment getSupportRequestManagerFragment(androidx.fragment.app.FragmentManager supportFragmentManager) {
        OSupportRequestManagerFragment fragmentByTag = (OSupportRequestManagerFragment) supportFragmentManager.findFragmentByTag(FRAGMENT_SUPPORT_TAG);
        if (null == fragmentByTag) {
            fragmentByTag = new OSupportRequestManagerFragment();
            supportFragmentManager.beginTransaction().add(fragmentByTag, FRAGMENT_SUPPORT_TAG).commitAllowingStateLoss();
            //需要使用handler发送一条消息，使其添加成功
            handler.obtainMessage(ID_REMOVE_SUPPORT_FRAGMENT_MANAGER, supportFragmentManager).sendToTarget();
        }
        return fragmentByTag;
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }
}
