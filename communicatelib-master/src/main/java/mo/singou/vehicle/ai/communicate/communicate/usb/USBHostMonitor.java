package mo.singou.vehicle.ai.communicate.communicate.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.maikel.logger.LogTools;


public class USBHostMonitor extends BaseUSBManager<UsbDevice>{
    private static final String TAG = "USBHostMonitor";
    public USBHostMonitor(Context context,int type) {
        super(context,type);
    }

    @Override
    public void onAttach(UsbDevice device) {
        LogTools.p(TAG,"onAttach--->"+device.getProductId());
    }

    @Override
    public void onDettach(UsbDevice device) {
        LogTools.p(TAG,"onDettach--->"+device.getProductId());
    }

    @Override
    public void onConnect(UsbDevice device) {
        LogTools.p(TAG,"onConnect--->"+device.getProductId());
    }

    @Override
    public void onDisconnect(UsbDevice device) {
        LogTools.p(TAG,"onDisconnect--->"+device.getProductId());
    }

    @Override
    public void onCancel(UsbDevice device) {
        LogTools.p(TAG,"onCancel--->"+device.getProductId());
    }

    @Override
    public void setOnAddressListener(OnAddressListener listener) {

    }
}
