package mo.singou.vehicle.ai.communicate.usb;

public interface OnUsbDeviceStateListener<T> {
    void onAttached(T device);
    void onPermissionGranted(T device);
    void onPermissionCancel(T device);
    void onDetached(T device);
}
