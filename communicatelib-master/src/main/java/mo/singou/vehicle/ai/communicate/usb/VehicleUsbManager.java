package mo.singou.vehicle.ai.communicate.usb;

import android.content.Context;

public class VehicleUsbManager implements IUSB{
    private IUSB mUsbManager;
    private static VehicleUsbManager vehicleUsbManager;
    private VehicleUsbManager(IUSB iusb){
        mUsbManager = iusb;
    }
    public static final VehicleUsbManager getInstance(int type, Context context){
        if (vehicleUsbManager == null){
            synchronized (VehicleUsbManager.class){
                if (vehicleUsbManager == null){
                    switch (type){
                        case TYPE_HOST:
                            vehicleUsbManager = new VehicleUsbManager(USBHostManager.getInstance(context));
                            break;
                        case TYPE_ACCESSORY:
                            vehicleUsbManager = new VehicleUsbManager(USBAccessoryManager.getInstance(context));
                            break;
                    }
                }
            }
        }
        return vehicleUsbManager;
    }

    @Override
    public void init() {
        mUsbManager.init();
    }

    @Override
    public void unInit() {
        mUsbManager.unInit();
    }

    @Override
    public void sendMessage(String serial,String message) {
        mUsbManager.sendMessage(serial,message);
    }

    @Override
    public void sendMessage(String serial,byte[] message) {
        mUsbManager.sendMessage(serial,message);
    }

    @Override
    public void destroy() {
        mUsbManager.destroy();
    }


    @Override
    public void setOnDeviceConnectListener(OnDeviceConnectListener listener) {
        mUsbManager.setOnDeviceConnectListener(listener);
    }
}
