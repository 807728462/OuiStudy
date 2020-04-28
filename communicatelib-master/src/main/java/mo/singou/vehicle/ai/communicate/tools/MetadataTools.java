package mo.singou.vehicle.ai.communicate.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;

public class MetadataTools {

    private MetadataTools(){}

    public static final String getApplicationMetaValue(Context context,String key){
        try {
            ApplicationInfo  appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object value = appInfo.metaData.get(key);
            if (value==null){
                value = ""+appInfo.metaData.getInt(key);
            }
            return String.valueOf(value);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String getActivityMetaValue(Context context,String key,Class<?> activityClz){
        try {
            ComponentName cn = new ComponentName(context, activityClz);
            ActivityInfo appInfo = context.getPackageManager()
                    .getActivityInfo(cn,PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static final String getReceiverMetaValue(Context context,String key,Class<?> receiverClz){
        try {
            ComponentName cn = new ComponentName(context, receiverClz);
            ActivityInfo  appInfo = context.getPackageManager()
                    .getReceiverInfo(cn,PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String getServiceMetaValue(Context context,String key,Class<?> serviceClz){
        try {
            ComponentName cn= new ComponentName(context, serviceClz);
            ServiceInfo appInfo = context.getPackageManager()
                    .getServiceInfo(cn,PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
