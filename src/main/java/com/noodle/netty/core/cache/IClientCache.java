package com.noodle.netty.core.cache;

import java.util.Collection;

import com.noodle.netty.core.dto.ClientDTO;

import io.netty.channel.Channel;


/**
 * 客户端缓存
 * @author liulxiang
 * @date 2025/10/25 13:15
 */
public interface IClientCache {
    Collection<ClientDTO> getAllList();
    // 客户端总数量
    int count();
    // 添加客户端
    void add(Channel channel,String message);
    // 删除客户端
    void remove(Channel channel,String message);
    // 提取客户端
    ClientDTO get(String clientKey);
}
