package com.noodle.netty.core.enums;


/**
 * 协议类型
 *
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public enum ServerTypeEnum {
    MQTT("MQTT"),
    WS("WS"),
    ;
    public String TYPE;

    ServerTypeEnum(String type) {
        this.TYPE = type;
    }

}
