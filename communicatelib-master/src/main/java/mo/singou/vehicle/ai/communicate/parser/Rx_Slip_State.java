package mo.singou.vehicle.ai.communicate.parser;

public enum  Rx_Slip_State {
    rxstate_init_nosync, /* 暂未用. */
    rxstate_nosync, /* 初始一包数据未开始. */
    rxstate_start, /* 开始解析数据包长度 */
    rxstate_body, /* 消息体. */
    rxstate_body_esc /* 停止解析. */
}
