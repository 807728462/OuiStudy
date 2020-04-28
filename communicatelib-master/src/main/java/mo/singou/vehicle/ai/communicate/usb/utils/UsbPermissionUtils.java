package mo.singou.vehicle.ai.communicate.usb.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.lang.reflect.Method;

public class UsbPermissionUtils {
    private UsbPermissionUtils(){}
    /**
     * USB自动授权
     *
     * @param usbDevice
     * @param context
     * @throws Exception
     */
    public static void grantAutomaticPermission(UsbDevice usbDevice, Context context) throws Exception {

        PackageManager pkgManager = context.getPackageManager();
        ApplicationInfo appInfo = pkgManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

        Class serviceManagerClass = Class.forName("android.os.ServiceManager");
        Method getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String.class);
        getServiceMethod.setAccessible(true);
        android.os.IBinder binder = (android.os.IBinder) getServiceMethod.invoke(null, Context.USB_SERVICE);

        Class iUsbManagerClass = Class.forName("android.hardware.usb.IUsbManager");
        Class stubClass = Class.forName("android.hardware.usb.IUsbManager$Stub");
        Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", android.os.IBinder.class);
        asInterfaceMethod.setAccessible(true);
        Object iUsbManager = asInterfaceMethod.invoke(null, binder);

        final Method grantDevicePermissionMethod = iUsbManagerClass.getDeclaredMethod("grantDevicePermission", UsbDevice.class, int.class);
        grantDevicePermissionMethod.setAccessible(true);
        grantDevicePermissionMethod.invoke(iUsbManager, usbDevice, appInfo.uid);

    }

    /**
     * USB自动授权
     *
     * @param usbDevice
     * @param context
     * @throws Exception
     */
    public static void grantAutomaticPermission(UsbAccessory usbDevice, Context context) throws Exception {

        PackageManager pkgManager = context.getPackageManager();
        ApplicationInfo appInfo = pkgManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

        Class serviceManagerClass = Class.forName("android.os.ServiceManager");
        Method getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String.class);
        getServiceMethod.setAccessible(true);
        android.os.IBinder binder = (android.os.IBinder) getServiceMethod.invoke(null, Context.USB_SERVICE);

        Class iUsbManagerClass = Class.forName("android.hardware.usb.IUsbManager");
        Class stubClass = Class.forName("android.hardware.usb.IUsbManager$Stub");
        Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", android.os.IBinder.class);
        asInterfaceMethod.setAccessible(true);
        Object iUsbManager = asInterfaceMethod.invoke(null, binder);

        final Method grantDevicePermissionMethod = iUsbManagerClass.getDeclaredMethod("grantAccessoryPermission", UsbAccessory.class, int.class);
        grantDevicePermissionMethod.setAccessible(true);
        grantDevicePermissionMethod.invoke(iUsbManager, usbDevice, appInfo.uid);

    }
    /**
     * request permission to access to USBHostManager device
     *
     * @param device
     * @return true if fail to request permission
     */
    public static void requestPermission(final UsbManager manager, final UsbDevice device, PendingIntent pendingIntent) {
        if(manager==null || device==null ||pendingIntent==null ){
            return;
        }
        manager.requestPermission(device, pendingIntent);
    }

    /**
     * request permission to access to USBHostManager device
     *
     * @param device
     * @return true if fail to request permission
     */
    public static void requestPermission(final UsbManager manager, final UsbAccessory device, PendingIntent pendingIntent) {
        if(manager==null || device==null ||pendingIntent==null ){
            return;
        }
        manager.requestPermission(device, pendingIntent);
    }
}
