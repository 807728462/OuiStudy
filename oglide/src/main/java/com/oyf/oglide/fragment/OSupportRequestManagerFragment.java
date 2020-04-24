package com.oyf.oglide.fragment;

import androidx.fragment.app.Fragment;

import com.oyf.oglide.ORequestManager;
import com.oyf.oglide.lifecycle.OActivityFragmentLifecycle;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 14:25
 * @描述
 **/
public class OSupportRequestManagerFragment extends Fragment {
    private final OActivityFragmentLifecycle lifecycle;
    private ORequestManager mORequestManager;

    public OSupportRequestManagerFragment() {
        this(new OActivityFragmentLifecycle());
    }

    public OSupportRequestManagerFragment(OActivityFragmentLifecycle oActivityFragmentLifecycle) {
        lifecycle = oActivityFragmentLifecycle;
    }

    public ORequestManager getORequestManager() {
        return mORequestManager;
    }

    public void setORequestManager(ORequestManager ORequestManager) {
        mORequestManager = ORequestManager;
    }

    public OActivityFragmentLifecycle getGlideLifecycle() {
        return lifecycle;
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }
}
