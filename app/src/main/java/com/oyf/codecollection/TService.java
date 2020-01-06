package com.oyf.codecollection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder() {
            TService getService() {
                return TService.this;
            }
        };
    }

    class a extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
