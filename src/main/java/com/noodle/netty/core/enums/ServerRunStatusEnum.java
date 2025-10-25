package com.noodle.netty.core.enums;

/**
 * 服务运行状态
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public enum  ServerRunStatusEnum {
    // 已关闭
    CLOSE(0),
    // 运行中
    RUN_ING(1),
    // 关闭中，还有线程运行
    CLOSE_ING(2),
    ;
    public Integer STATUS;

    ServerRunStatusEnum(Integer status) {
        this.STATUS = status;
    }
}
