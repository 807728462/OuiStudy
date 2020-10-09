package com.oyf.codecollection;

import android.app.Application;

import com.alibaba.android.alpha.AlphaManager;
import com.alibaba.android.alpha.Project;
import com.alibaba.android.alpha.Task;
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
        Project.Builder builder = new Project.Builder();
        Task task1 = new Task("111111", false) {
            @Override
            public void run() {
                BlockCanary.install(App.this, new AppContext()).start();
                PxAdapterUtil.init(App.this);
                PluginUtils.hookIActivityManager();
                PluginUtils.hookHandler();

                LogUtils.init(App.this);
            }
        };
        builder.add(task1);
     /*   builder.add(new Task("3333333", false) {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    LogUtils.d("----------------", Thread.currentThread() + "-------------33333333333333");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).after(task1);
        builder.add(new Task("22222", false) {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    LogUtils.d("----------------", Thread.currentThread() + "-------------222222222222222");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).after(task1);*/
        builder.setProjectName("innerGroup");
        Project project = builder.create();
        AlphaManager.getInstance(this).addProject(project);
        AlphaManager.getInstance(this).start();

    }
}
