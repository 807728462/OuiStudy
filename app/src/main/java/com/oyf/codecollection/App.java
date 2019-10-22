package com.oyf.codecollection;

import android.app.Application;

import com.github.moduth.blockcanary.BlockCanary;
import com.oyf.codecollection.utils.AppContext;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlockCanary.install(this, new AppContext()).start();
    }
}
