package mo.singou.vehicle.ai.communicate.usb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.maikel.logger.LogTools;

import mo.singou.vehicle.ai.communicate.usb.IUSB;
import mo.singou.vehicle.ai.communicate.usb.OnUsbDeviceStateListener;


public class UsbHostReceiver extends BroadcastReceiver {

    private static final String TAG = UsbHostReceiver.class.getSimpleName();
    private OnUsbDeviceStateListener<UsbDevice> mListener;
    public UsbHostReceiver(OnUsbDeviceStateListener<UsbDevice> listener){
        this.mListener = listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        LogTools.p(TAG, "usb 通知-->" + action);
        if (IUSB.ACTION_USB_HOST_PERMISSION.equals(action)) {
            boolean isGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
            if (isGranted){
                mListener.onPermissionGranted(device);
            }else {
                mListener.onPermissionCancel(device);
            }
        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            mListener.onAttached(device);
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            mListener.onDetached(device);
        }
    }
}
