package com.noodle.netty.core.cache;

import java.util.HashMap;
import java.util.Map;

import com.noodle.netty.core.enums.ServerTypeEnum;
import com.noodle.netty.core.server.IServer;

/**
 * 服务列表缓存
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public interface IServerListCache {
    // 运行中服务列表
    Map<String, IServer> SERVER_LIST_MAP = new HashMap<>();

    static IServer getServer(ServerTypeEnum serverTypeEnum){
        return SERVER_LIST_MAP.get(serverTypeEnum.TYPE);
    }
}
