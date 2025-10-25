package com.noodle.netty.core.service;

import com.noodle.netty.core.cache.IServerListCache;
import com.noodle.netty.core.enums.ServerTypeEnum;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务逻辑父类
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
@Slf4j
@Setter
public abstract class AbsDataService implements IDataService {
    @Override
    public void push(String userOnlyCode, String message) {
    }

    @Override
    public void pushTopicAll(String message,String... topics ) {
        for (String topic: topics) {
            IServerListCache.getServer(ServerTypeEnum.MQTT).sendAll(topic,message);
        }
    }

    @Override
    public void pushAll(String message) {

    }
}
