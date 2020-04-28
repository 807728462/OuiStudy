package mo.singou.vehicle.ai.communicate.communicate.usb;

import android.content.Context;
import android.hardware.usb.UsbAccessory;

import com.maikel.logger.LogTools;


public class USBAccessoryManager extends BaseUSBManager<UsbAccessory>{
    private static final String TAG = "USBAccessoryManager";
    public USBAccessoryManager(Context context,int type) {
        super(context,type);
    }

    @Override
    public void onAttach(UsbAccessory device) {
        LogTools.p(TAG,"onAttach--->"+device.getManufacturer());
    }

    @Override
    public void onDettach(UsbAccessory device) {
        LogTools.p(TAG,"onDettach--->"+device.getManufacturer());
    }

    @Override
    public void onConnect(UsbAccessory device) {
        LogTools.p(TAG,"onConnect--->"+device.getManufacturer());
    }

    @Override
    public void onDisconnect(UsbAccessory device) {
        LogTools.p(TAG,"onDisconnect--->"+device.getManufacturer());
    }

    @Override
    public void onCancel(UsbAccessory device) {
        LogTools.p(TAG,"onCancel--->"+device.getManufacturer());
    }

    @Override
    public void setOnAddressListener(OnAddressListener listener) {

    }
}
