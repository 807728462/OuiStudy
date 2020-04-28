package mo.singou.vehicle.ai.communicate.usb;

/**
 * USB通信接口
 */
public interface IUSB {
    int TYPE_HOST = 1;
    int TYPE_ACCESSORY = 2;
    String ACTION_USB_HOST_PERMISSION = "mo.singou.vehicle.ai.usb.host.USB_PERMISSION";
    String ACTION_USB_ACCESSORY_PERMISSION = "mo.singou.vehicle.ai.usb.accessory.USB_PERMISSION";
    /**
     * 消息发送超时时间
     **/
    long rpcMsgTimeOutTime = 15 * 1000;

    /**
     * 初始化
     */
    void init();

    /**
     * 反初始化
     */
    void unInit();

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(String serial, String message);

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(String serial, byte[] message);

    /**
     * 销毁
     */
    void destroy();

    /**
     * 设置USB设备状态
     *
     * @param listener 监听接口
     */
    void setOnDeviceConnectListener(OnDeviceConnectListener listener);

    interface OnDeviceConnectListener<T> {
        /**
         * usb设备插入回调
         *
         * @param device 设备(UsbDevice或者UsbAccessory)
         */
        void onAttach(T device);

        /**
         * usb设备设备拔出回调
         *
         * @param device 设备(UsbDevice或者UsbAccessory)
         */
        void onDettach(T device);

        /**
         * 设备连接成功回调
         *
         * @param device 设备 (UsbDevice或者UsbAccessory)
         */
        void onConnect(T device);

        /**
         * 当usbhostmanager设备移除或关闭电源时调用（此回调在设备关闭后调用）
         *
         * @param device 设备(UsbDevice或者UsbAccessory)
         */
        void onDisconnect(T device);

        /**
         * 在取消或无法从用户获取权限时调用
         *
         * @param device 设备(UsbDevice或者UsbAccessory)
         */
        void onCancel(T device);

        /**
         * 消息监听回调
         *
         * @param serial 序列号
         * @param eventId 消息ID
         * @param message 消息
         */
        void onMessage(String serial,int eventId,String message);

        void onTimeOut(String serial);

        void onError(String serial, String error);
    }
}
