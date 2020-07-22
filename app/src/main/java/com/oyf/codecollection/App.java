package com.oyf.codecollection;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.oyf.basemodule.log.LogUtils;
import com.oyf.basemodule.utils.PxAdapterUtil;
import com.oyf.codecollection.test.Son;
import com.oyf.codecollection.utils.AppContext;
import com.oyf.plugin.utils.PluginUtils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlockCanary.install(this, new AppContext()).start();
        PxAdapterUtil.init(this);
        PluginUtils.hookIActivityManager();
        PluginUtils.hookHandler();

        LogUtils.init(this);
        new Son();
    }
}
