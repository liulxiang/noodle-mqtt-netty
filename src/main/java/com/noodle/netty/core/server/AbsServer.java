package com.noodle.netty.core.server;

/**
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public abstract class AbsServer<T> implements IServer {
    // 服务器参数
    protected T serverConfig;
}
